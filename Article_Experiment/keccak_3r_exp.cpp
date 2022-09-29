//
//  main.cpp
//  3r experiments on keccak-512
//  
//  Created on 2022/9/3.
//

#include <iostream>
#include <cstdlib>
#include <ctime>
#include <map>
#include <cmath>
#include <vector>

using namespace std;

typedef unsigned char UINT8;
typedef unsigned long int UINT32;
typedef unsigned long long int UINT64;


#define random(x) (rand())%x;
#define nrRounds 3
UINT64 KeccakRoundConstants[nrRounds];//these are constant,
#define nrLanes 25
unsigned int KeccakRhoOffsets[nrLanes];//these are constant,

#define index(x, y) (((x)%5)+5*((y)%5))
#define ROL64(a, offset) ((offset != 0) ? ((((UINT64)a) << offset) ^ (((UINT64)a) >> (64-offset))) : a)
#define ROR64(a, offset) ((offset != 0) ? ((((UINT64)a) >> offset) ^ (((UINT64)a) << (64-offset))) : a)
#define TAKE_BIT(x, pos) (((x) >> (pos)) & 0x1)


void KeccakPermutationOnWords(UINT64 *state);
void theta(UINT64 *A);
void rho(UINT64 *A);
void rho_inv(UINT64 *A);
void pi(UINT64 *A);
void chi(UINT64 *A);
void chi_inv(UINT64 *A);
void iota(UINT64 *A, unsigned int indexRound);


void KeccakPermutationOnWords(UINT64 state[], int round)
{
    unsigned int i;


    for(i=0; i<round; i++) {
        theta(state);
        rho(state);
        pi(state);
        chi(state);
        iota(state, i);
    }
}


void theta(UINT64 *A)
{
    unsigned int x, y;
    UINT64 C[5], D[5];//C are the Xors of the five bits in every column. D are the Xors of the ten bits in right-behind column and right column

    for(x=0; x<5; x++) {
        C[x] = 0;
        for(y=0; y<5; y++)
            C[x] ^= A[index(x, y)];
    }
    for(x=0; x<5; x++)
        D[x] = ROL64(C[(x+1)%5], 1) ^ C[(x+4)%5];
    for(x=0; x<5; x++)
        for(y=0; y<5; y++)
            A[index(x, y)] ^= D[x];
}

void rho(UINT64 *A)
{
    unsigned int x, y;

    for(x=0; x<5; x++)
        for(y=0; y<5; y++)
            A[index(x, y)] = ROL64(A[index(x, y)], KeccakRhoOffsets[index(x, y)]);
}

void rho_inv(UINT64 *A)
{
    unsigned int x;

    for(x=0; x<5; x++)
        A[index(x, 0)] = ROR64(A[index(x, 0)], KeccakRhoOffsets[index(x, x)]);
}

void pi(UINT64 *A)
{
    unsigned int x, y;
    UINT64 tempA[25];

    for(x=0; x<5; x++)
        for(y=0; y<5; y++)
            tempA[index(x, y)] = A[index(x, y)];
    for(x=0; x<5; x++)
        for(y=0; y<5; y++)
            A[index(0*x+1*y, 2*x+3*y)] = tempA[index(x, y)];
}

void pi_inv(UINT64 *A)
{
    unsigned int x;
    UINT64 tempA[25];

    for(x=0; x<5; x++)
        tempA[index(x, 0)] = A[index(x, 0)];
    for(x=0; x<5; x++)
        A[index(x, x)] = tempA[index(x, 0)];
}

void chi(UINT64 *A)
{
    unsigned int x, y;
    UINT64 C[5];

    for(y=0; y<5; y++) {
        for(x=0; x<5; x++)
            C[x] = A[index(x, y)] ^ ((~A[index(x+1, y)]) & A[index(x+2, y)]);
        for(x=0; x<5; x++)
            A[index(x, y)] = C[x];
    }
}

void chi_inv(UINT64 *A)
{
    unsigned int x;
    UINT64 C[5];
    
    for(x=0; x<5; x++)
        C[x] = A[index(x, 0)] ^ ((~A[index(x+1, 0)]) & (A[index(x+2, 0)] ^ ((~A[index(x+3, 0)]) & A[index(x+4, 0)])));
    for(x=0; x<5; x++)
        A[index(x, 0)] = C[x];
    
}

void iota(UINT64 *A, unsigned int indexRound)
{
    A[index(0, 0)] ^= KeccakRoundConstants[indexRound];
}



int LFSR86540(UINT8 *LFSR)
{
    int result = ((*LFSR) & 0x01) != 0;
    if (((*LFSR) & 0x80) != 0)
        // Primitive polynomial over GF(2): x^8+x^6+x^5+x^4+1
        (*LFSR) = ((*LFSR) << 1) ^ 0x71;
    else
        (*LFSR) <<= 1;
    return result;
}

void KeccakInitializeRoundConstants()
{
    UINT8 LFSRstate = 0x01;
    unsigned int i, j, bitPosition;

    for(i=0; i<nrRounds; i++) {
        KeccakRoundConstants[i] = 0;
        for(j=0; j<7; j++) {
            bitPosition = (1<<j)-1; //2^j-1
            if (LFSR86540(&LFSRstate))
                KeccakRoundConstants[i] ^= ((UINT64)1<<bitPosition);
        }
    }
}

void KeccakInitializeRhoOffsets()
{
    unsigned int x, y, t, newX, newY;

    KeccakRhoOffsets[index(0, 0)] = 0;
    x = 1;
    y = 0;
    for(t=0; t<24; t++) {
        KeccakRhoOffsets[index(x, y)] = ((t+1)*(t+2)/2) % 64;
        newX = (0*x+1*y) % 5;
        newY = (2*x+3*y) % 5;
        x = newX;
        y = newY;
    }
    //for (x=0;x<5;x++)
        //for (y=0;y<5;y++)
            //cout << KeccakRhoOffsets[index(x, y)] << endl;
}

void KeccakInitialize()
{
    KeccakInitializeRoundConstants();
    KeccakInitializeRhoOffsets();
}

void displaystate(UINT64 *state, int lanes)
{
    unsigned int i;
    for(i=0;i<lanes;i++)
    {
        //printf("%08x ",(unsigned int)(state[i]));
        printf("%016llx ",(state[i]));
        if((i+1)%5==0) printf("\n");
    }
    printf("\n");
}


int main(int argc, const char * argv[])
{
    srand((unsigned)time(NULL));
    
    KeccakInitialize();
    
    unsigned int indexb[8][2]={0};
    unsigned int indexr[8][2]={0};
    unsigned int index1[16][2]={0};
    unsigned int index0[16][2]={0};
    
    indexr[0][0]=0; indexr[0][1]=2;
    indexr[1][0]=2; indexr[1][1]=13;
    indexr[2][0]=0; indexr[2][1]=17;
    indexr[3][0]=2; indexr[3][1]=32;
    indexr[4][0]=2; indexr[4][1]=37;
    indexr[5][0]=2; indexr[5][1]=40;
    indexr[6][0]=0; indexr[6][1]=48;
    indexr[7][0]=0; indexr[7][1]=63;
    

    
    indexb[0][0]=0; indexb[0][1]=8;
    indexb[1][0]=0; indexb[1][1]=9;
    indexb[2][0]=0; indexb[2][1]=13;
    indexb[3][0]=0; indexb[3][1]=41;
    indexb[4][0]=2; indexb[4][1]=45;
    indexb[5][0]=2; indexb[5][1]=49;
    indexb[6][0]=2; indexb[6][1]=62;
    indexb[7][0]=2; indexb[7][1]=1;
    
    
    index0[0][0]=6; index0[0][1]=22; //+5 +6
    index0[1][0]=6; index0[1][1]=37;
    index0[2][0]=6; index0[2][1]=4;
    index0[3][0]=6; index0[3][1]=19;
    index0[4][0]=6; index0[4][1]=28;
    index0[5][0]=6; index0[5][1]=29;
    index0[6][0]=6; index0[6][1]=33;
    index0[7][0]=6; index0[7][1]=61;
    
    index0[8][0]=8; index0[8][1]=20;//+5 +38
    index0[9][0]=8; index0[9][1]=39;
    index0[10][0]=8; index0[10][1]=44;
    index0[11][0]=8; index0[11][1]=47;
    index0[12][0]=8; index0[12][1]=52;
    index0[13][0]=8; index0[13][1]=56;
    index0[14][0]=8; index0[14][1]=5;
    index0[15][0]=8; index0[15][1]=8;
    
    
    index1[0][0]=24; index1[0][1]=52; //-20 -41
    index1[1][0]=24; index1[1][1]=3;
    index1[2][0]=24; index1[2][1]=34;
    index1[3][0]=24; index1[3][1]=49;
    index1[4][0]=24; index1[4][1]=58;
    index1[5][0]=24; index1[5][1]=59;
    index1[6][0]=24; index1[7][1]=63;
    index1[7][0]=24; index1[7][1]=27;
    
    index1[8][0]=21; index1[8][1]=9;//-20 -55
    index1[9][0]=21; index1[9][1]=28;
    index1[10][0]=21; index1[10][1]=33;
    index1[11][0]=21; index1[11][1]=36;
    index1[12][0]=21; index1[12][1]=41;
    index1[13][0]=21; index1[13][1]=45;
    index1[14][0]=21; index1[14][1]=58;
    index1[15][0]=21; index1[15][1]=61;
    
    UINT64 InitialState[25]={0};
    UINT64 TempState[25]={0};
    UINT64 FinalState[8]={0};
    UINT64 ThetaState[5]={0};

    
    UINT64 rightkey=0;
    
    //Init the 1600-bit state with 0
    for(UINT64 i=0;i<25;i++){
        InitialState[i]=0;
    }
    //Randomly chosse the gray bits A_{0,0,z}=A_{0,1,z}, A_{2,0,z}=A_{2,1,z},
    for(UINT64 i=0;i<64;i++){
        UINT64 temp=random(2);
        if(temp){
            InitialState[0] |= (UINT64(1)<<i);
            InitialState[5] |= (UINT64(1)<<i);
        }
        temp=random(2);
        if(temp){
            InitialState[2] |= (UINT64(1)<<i);
            InitialState[7] |= (UINT64(1)<<i);
        }
    }
    
    //Randomly set red bits and blue bits in M2
    for(UINT64 i=0;i<8;i++){
        UINT64 temp=random(2);
        if(temp){
            InitialState[indexr[i][0]] |= (UINT64(1)<<indexr[i][1]);
            InitialState[indexr[i][0]+5] |= (UINT64(1)<<indexr[i][1]);
        }
        else {
            InitialState[indexr[i][0]] &= ROL64(~UINT64(1),indexr[i][1]);
            InitialState[indexr[i][0]+5] &= ROL64(~UINT64(1),indexr[i][1]);
        }
        //generate the right key
        rightkey = (rightkey<<1) ^ temp;
    }
    
    for(UINT64 i=0;i<8;i++){
        UINT64 temp=random(2);
        if(temp){
            InitialState[indexb[i][0]] |= (UINT64(1)<<indexb[i][1]);
            InitialState[indexb[i][0]+5] |= (UINT64(1)<<indexb[i][1]);
        }
        else {
            InitialState[indexb[i][0]] &= ROL64(~UINT64(1),indexb[i][1]);
            InitialState[indexb[i][0]+5] &= ROL64(~UINT64(1),indexb[i][1]);
        }
        //generate the right key
        rightkey = (rightkey<<1) ^ temp;
    }
    
    //Set two bits padding
    InitialState[8] |= (UINT64(1)<<63);
    InitialState[3] |= (UINT64(1)<<63);
    InitialState[8] |= (UINT64(1)<<62);
    InitialState[3] |= (UINT64(1)<<62);
    
    //Set 64 bit conditions satified with M1
    for(UINT64 i=0;i<8;i++)
    {
        //set conditions=1
        InitialState[index1[i][0]] |= (UINT64(1)<<index1[i][1]);
        InitialState[index1[i][0]-20] |= (UINT64(1)<<((index1[i][1]-41+64)%64));
        InitialState[index1[i+8][0]] |= (UINT64(1)<<index1[i+8][1]);
        InitialState[index1[i+8][0]-20] |= (UINT64(1)<<((index1[i+8][1]-55+64)%64));
        
        //set conditions=0
        InitialState[index0[i][0]] &= ROL64(~UINT64(1),index0[i][1]);
        InitialState[index0[i][0]+5] &= ROL64(~UINT64(1),(index0[i][1]+6)%64);
        InitialState[index0[i+8][0]] &= ROL64(~UINT64(1),index0[i+8][1]);
        InitialState[index0[i+8][0]+5] &= ROL64(~UINT64(1),(index0[i+8][1]+38)%64);
    }

    //displaystate(InitialState,25);
    
    //Computer the hash value
    for(UINT64 i=0;i<25;i++){
        TempState[i]=InitialState[i];
    }
    KeccakPermutationOnWords(TempState, 3);
    //displaystate(TempState,25);
    for(UINT64 i=0;i<8;i++){
        FinalState[i]=TempState[i];
    }
    
    //Inverse the Sbox
    for(UINT64 i=0;i<5;i++){
        ThetaState[i]=TempState[i];
    }
    iota(ThetaState, 2);
    chi_inv(ThetaState);
    rho_inv(ThetaState);
    //displaystate(ThetaState,5);
   
    map<UINT64, vector<UINT64>> TableU;
    //Generate table U
    for(UINT64 j=0;j<(UINT64(1)<<8);j++){
        for(UINT64 k=0;k<25;k++){
            TempState[k]=InitialState[k];
        }
        //Set blue=0
        for(UINT64 k=0;k<8;k++){
            TempState[indexb[k][0]] &= ROL64(~UINT64(1),indexb[k][1]);
            TempState[indexb[k][0]+5] &= ROL64(~UINT64(1),indexb[k][1]);
        }
        //Traverse the red bits
        for(UINT64 k=0;k<8;k++) {
            UINT64 temp1=(j>>(7-k))&1;
            if(temp1){
                TempState[indexr[k][0]] |= (UINT64(1)<<indexr[k][1]);
                TempState[indexr[k][0]+5] |= (UINT64(1)<<indexr[k][1]);
            }
            else {
                TempState[indexr[k][0]] &= ROL64(~UINT64(1),indexr[k][1]);
                TempState[indexr[k][0]+5] &= ROL64(~UINT64(1),indexr[k][1]);
            }
                    
        }
        
        //Compute A^(2)
        KeccakPermutationOnWords(TempState, 2);
        
        
        UINT64 Matchpoint[11] = {0};
        UINT64 MatchRandG = 0;
        //Compute f'm
        Matchpoint[0] = TAKE_BIT(TempState[3],36) ^ TAKE_BIT(TempState[18],36) ^ ((TAKE_BIT(FinalState[6],0)^1) & (TAKE_BIT(TempState[10],61)^TAKE_BIT(TempState[0],61))) ^ TAKE_BIT(FinalState[5],0) ^ TAKE_BIT(ThetaState[3],36) ^ ((TAKE_BIT(FinalState[6],0)^1) & TAKE_BIT(ThetaState[0],61));
        MatchRandG = Matchpoint[0] & 0x1;
    
        Matchpoint[1] = TAKE_BIT(TempState[3],58) ^ TAKE_BIT(TempState[18],58) ^ ((TAKE_BIT(FinalState[6],22)^1) & (TAKE_BIT(TempState[10],19)^TAKE_BIT(TempState[0],19))) ^ TAKE_BIT(FinalState[5],22) ^ TAKE_BIT(ThetaState[3],58) ^ ((TAKE_BIT(FinalState[6],22)^1) & TAKE_BIT(ThetaState[0],19));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[1] & 0x1);
    
        Matchpoint[2] = TAKE_BIT(TempState[3],2) ^ TAKE_BIT(TempState[18],2) ^ ((TAKE_BIT(FinalState[6],30)^1) & (TAKE_BIT(TempState[10],27)^TAKE_BIT(TempState[0],27))) ^ TAKE_BIT(FinalState[5],30) ^ TAKE_BIT(ThetaState[3],2) ^ ((TAKE_BIT(FinalState[6],30)^1) & TAKE_BIT(ThetaState[0],27));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[2] & 0x1);
    
        Matchpoint[3] = TAKE_BIT(TempState[3],27) ^TAKE_BIT(TempState[18],27) ^ ((TAKE_BIT(FinalState[6],55)^1) & (TAKE_BIT(TempState[10],52)^TAKE_BIT(TempState[0],52))) ^ TAKE_BIT(FinalState[5],55) ^ TAKE_BIT(ThetaState[3],27) ^ ((TAKE_BIT(FinalState[6],55)^1) & TAKE_BIT(ThetaState[0],52)) ;
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[3] & 0x1);
        
        Matchpoint[4] = TAKE_BIT(TempState[9],1) ^ TAKE_BIT(TempState[24],1) ^ ((TAKE_BIT(FinalState[7],21)^1) & (TAKE_BIT(TempState[16],40)^TAKE_BIT(TempState[6],40))) ^ TAKE_BIT(FinalState[6],21) ^ TAKE_BIT(ThetaState[4],1) ^ ((TAKE_BIT(FinalState[7],21)^1) & TAKE_BIT(ThetaState[1],40));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[4] & 0x1);
    
        Matchpoint[5] = TAKE_BIT(TempState[9],4) ^ TAKE_BIT(TempState[24],4) ^ ((TAKE_BIT(FinalState[7],24)^1) & (TAKE_BIT(TempState[16],43)^TAKE_BIT(TempState[6],43))) ^ TAKE_BIT(FinalState[6],24) ^ TAKE_BIT(ThetaState[4],4) ^ ((TAKE_BIT(FinalState[7],24)^1) & TAKE_BIT(ThetaState[1],43));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[5] & 0x1);
    
        Matchpoint[6] = TAKE_BIT(TempState[9],5) ^ TAKE_BIT(TempState[24],5) ^ ((TAKE_BIT(FinalState[7],25)^1) & (TAKE_BIT(TempState[16],44)^TAKE_BIT(TempState[6],44))) ^ TAKE_BIT(FinalState[6],25) ^ TAKE_BIT(ThetaState[4],5) ^ ((TAKE_BIT(FinalState[7],25)^1) & TAKE_BIT(ThetaState[1],44));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[6] & 0x1);
    
        Matchpoint[7] = TAKE_BIT(TempState[9],17) ^ TAKE_BIT(TempState[24],17) ^ ((TAKE_BIT(FinalState[7],37)^1) & (TAKE_BIT(TempState[16],56)^TAKE_BIT(TempState[6],56))) ^ TAKE_BIT(FinalState[6],37) ^ TAKE_BIT(ThetaState[4],17) ^ ((TAKE_BIT(FinalState[7],37)^1) & TAKE_BIT(ThetaState[1],56));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[7] & 0x1) ;
    
        Matchpoint[8] = TAKE_BIT(TempState[9],20) ^ TAKE_BIT(TempState[24],20) ^ ((TAKE_BIT(FinalState[7],40)^1) & (TAKE_BIT(TempState[16],59)^TAKE_BIT(TempState[6],59))) ^ TAKE_BIT(FinalState[6],40) ^ TAKE_BIT(ThetaState[4],20) ^ ((TAKE_BIT(FinalState[7],40)^1) & TAKE_BIT(ThetaState[1],59));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[8] & 0x1);
    
        Matchpoint[9] = TAKE_BIT(TempState[9],41) ^ TAKE_BIT(TempState[24],41) ^ ((TAKE_BIT(FinalState[7],61)^1) & (TAKE_BIT(TempState[16],16)^TAKE_BIT(TempState[6],16))) ^ TAKE_BIT(FinalState[6],61) ^ TAKE_BIT(ThetaState[4],41) ^ ((TAKE_BIT(FinalState[7],61)^1) & TAKE_BIT(ThetaState[1],16));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[9] & 0x1);
    
        Matchpoint[10] = TAKE_BIT(TempState[9],43) ^ TAKE_BIT(TempState[24],43) ^ ((TAKE_BIT(FinalState[7],63)^1) & (TAKE_BIT(TempState[16],18)^TAKE_BIT(TempState[6],18))) ^ TAKE_BIT(FinalState[6],63) ^ TAKE_BIT(ThetaState[4],43) ^ ((TAKE_BIT(FinalState[7],63)^1) & TAKE_BIT(ThetaState[1],18));
        MatchRandG = (MatchRandG << 1) ^ (Matchpoint[10] & 0x1);

        
        //cout << MatchRandG << endl;
        //Store the value for 8 red bits
        if(TableU.find(MatchRandG) != TableU.end())
        {
            vector<UINT64> ttmp =TableU[MatchRandG];
            ttmp.push_back(j);
            TableU[MatchRandG] = ttmp;
        } else {
            vector<UINT64> ttmp;
            ttmp.push_back(j);
            TableU[MatchRandG] = ttmp;
        }
        
    }
    
    UINT64 MatchNum=0;
    //f'''m
    UINT64 Conste[11] = {0};
    //Fix Red, traverse the blue bits
    for(UINT64 j=0;j<(UINT64(1)<<8);j++){
        for(UINT64 k=0;k<25;k++){
            TempState[k]=InitialState[k];
        }
        
        for(UINT64 k=0;k<8;k++)
        {
            UINT64 temp1=(j>>(7-k))&1;
            if(temp1){
                TempState[indexb[k][0]] |= (UINT64(1)<<indexb[k][1]);
                TempState[indexb[k][0]+5] |= (UINT64(1)<<indexb[k][1]);
            }
            else{
                TempState[indexb[k][0]] &= ROL64(~UINT64(1),indexb[k][1]);
                TempState[indexb[k][0]+5] &= ROL64(~UINT64(1),indexb[k][1]);
            }
                    
        }
        KeccakPermutationOnWords(TempState, 2);
        
        UINT64 Matchpoint[11] = {0};
        UINT64 MatchB = 0;
        //Compute f''m
        Matchpoint[0] = TAKE_BIT(TempState[3],36) ^ TAKE_BIT(TempState[18],36) ^ ((TAKE_BIT(FinalState[6],0)^1) & (TAKE_BIT(TempState[10],61)^TAKE_BIT(TempState[0],61))) ^ TAKE_BIT(FinalState[5],0) ^ TAKE_BIT(ThetaState[3],36) ^ ((TAKE_BIT(FinalState[6],0)^1) & TAKE_BIT(ThetaState[0],61));
        //Compute f'''m
        if (j==0)
           Conste[0]=Matchpoint[0];
        //f''m^f'''m
        MatchB = (Matchpoint[0]^Conste[0]) & 0x1;
    
        Matchpoint[1] = TAKE_BIT(TempState[3],58) ^ TAKE_BIT(TempState[18],58) ^ ((TAKE_BIT(FinalState[6],22)^1) & (TAKE_BIT(TempState[10],19)^TAKE_BIT(TempState[0],19))) ^ TAKE_BIT(FinalState[5],22) ^ TAKE_BIT(ThetaState[3],58) ^ ((TAKE_BIT(FinalState[6],22)^1) & TAKE_BIT(ThetaState[0],19));
        if (j==0)
            Conste[1]=Matchpoint[1];
        MatchB = (MatchB << 1) ^ ((Matchpoint[1]^Conste[1]) & 0x1);
    
        Matchpoint[2] = TAKE_BIT(TempState[3],2) ^ TAKE_BIT(TempState[18],2) ^ ((TAKE_BIT(FinalState[6],30)^1) & (TAKE_BIT(TempState[10],27)^TAKE_BIT(TempState[0],27))) ^ TAKE_BIT(FinalState[5],30) ^ TAKE_BIT(ThetaState[3],2) ^ ((TAKE_BIT(FinalState[6],30)^1) & TAKE_BIT(ThetaState[0],27));
        if (j==0)
            Conste[2]=Matchpoint[2];
        MatchB = (MatchB << 1) ^ ((Matchpoint[2]^Conste[2]) & 0x1);
    
        Matchpoint[3] = TAKE_BIT(TempState[3],27) ^TAKE_BIT(TempState[18],27) ^ ((TAKE_BIT(FinalState[6],55)^1) & (TAKE_BIT(TempState[10],52)^TAKE_BIT(TempState[0],52))) ^ TAKE_BIT(FinalState[5],55) ^ TAKE_BIT(ThetaState[3],27) ^ ((TAKE_BIT(FinalState[6],55)^1) & TAKE_BIT(ThetaState[0],52)) ;
        if (j==0)
            Conste[3]=Matchpoint[3];
        MatchB = (MatchB << 1) ^ ((Matchpoint[3]^Conste[3]) & 0x1);
        
        Matchpoint[4] = TAKE_BIT(TempState[9],1) ^ TAKE_BIT(TempState[24],1) ^ ((TAKE_BIT(FinalState[7],21)^1) & (TAKE_BIT(TempState[16],40)^TAKE_BIT(TempState[6],40))) ^ TAKE_BIT(FinalState[6],21) ^ TAKE_BIT(ThetaState[4],1) ^ ((TAKE_BIT(FinalState[7],21)^1) & TAKE_BIT(ThetaState[1],40));
        if (j==0)
            Conste[4]=Matchpoint[4];
        MatchB = (MatchB << 1) ^ ((Matchpoint[4]^Conste[4]) & 0x1);
    
        Matchpoint[5] = TAKE_BIT(TempState[9],4) ^ TAKE_BIT(TempState[24],4) ^ ((TAKE_BIT(FinalState[7],24)^1) & (TAKE_BIT(TempState[16],43)^TAKE_BIT(TempState[6],43))) ^ TAKE_BIT(FinalState[6],24) ^ TAKE_BIT(ThetaState[4],4) ^ ((TAKE_BIT(FinalState[7],24)^1) & TAKE_BIT(ThetaState[1],43));
        if (j==0)
            Conste[5]=Matchpoint[5];
        MatchB = (MatchB << 1) ^ ((Matchpoint[5]^Conste[5]) & 0x1);
        
        Matchpoint[6] = TAKE_BIT(TempState[9],5) ^ TAKE_BIT(TempState[24],5) ^ ((TAKE_BIT(FinalState[7],25)^1) & (TAKE_BIT(TempState[16],44)^TAKE_BIT(TempState[6],44))) ^ TAKE_BIT(FinalState[6],25) ^ TAKE_BIT(ThetaState[4],5) ^ ((TAKE_BIT(FinalState[7],25)^1) & TAKE_BIT(ThetaState[1],44));
        if (j==0)
            Conste[6]=Matchpoint[6];
        MatchB = (MatchB << 1) ^ ((Matchpoint[6]^Conste[6]) & 0x1);
        
        Matchpoint[7] = TAKE_BIT(TempState[9],17) ^ TAKE_BIT(TempState[24],17) ^ ((TAKE_BIT(FinalState[7],37)^1) & (TAKE_BIT(TempState[16],56)^TAKE_BIT(TempState[6],56))) ^ TAKE_BIT(FinalState[6],37) ^ TAKE_BIT(ThetaState[4],17) ^ ((TAKE_BIT(FinalState[7],37)^1) & TAKE_BIT(ThetaState[1],56));
        if (j==0)
            Conste[7]=Matchpoint[7];
        MatchB = (MatchB << 1) ^ ((Matchpoint[7]^Conste[7]) & 0x1);
        
        Matchpoint[8] = TAKE_BIT(TempState[9],20) ^ TAKE_BIT(TempState[24],20) ^ ((TAKE_BIT(FinalState[7],40)^1) & (TAKE_BIT(TempState[16],59)^TAKE_BIT(TempState[6],59))) ^ TAKE_BIT(FinalState[6],40) ^ TAKE_BIT(ThetaState[4],20) ^ ((TAKE_BIT(FinalState[7],40)^1) & TAKE_BIT(ThetaState[1],59));
        if (j==0)
            Conste[8]=Matchpoint[8];
        MatchB = (MatchB << 1) ^ ((Matchpoint[8]^Conste[8]) & 0x1);
        
        Matchpoint[9] = TAKE_BIT(TempState[9],41) ^ TAKE_BIT(TempState[24],41) ^ ((TAKE_BIT(FinalState[7],61)^1) & (TAKE_BIT(TempState[16],16)^TAKE_BIT(TempState[6],16))) ^ TAKE_BIT(FinalState[6],61) ^ TAKE_BIT(ThetaState[4],41) ^ ((TAKE_BIT(FinalState[7],61)^1) & TAKE_BIT(ThetaState[1],16));
        if (j==0)
            Conste[9]=Matchpoint[9];
        MatchB = (MatchB << 1) ^ ((Matchpoint[9]^Conste[9]) & 0x1);
        
        Matchpoint[10] = TAKE_BIT(TempState[9],43) ^ TAKE_BIT(TempState[24],43) ^ ((TAKE_BIT(FinalState[7],63)^1) & (TAKE_BIT(TempState[16],18)^TAKE_BIT(TempState[6],18))) ^ TAKE_BIT(FinalState[6],63) ^ TAKE_BIT(ThetaState[4],43) ^ ((TAKE_BIT(FinalState[7],63)^1) & TAKE_BIT(ThetaState[1],18));
        if (j==0)
            Conste[10]=Matchpoint[10];
        MatchB = (MatchB << 1) ^ ((Matchpoint[10]^Conste[10]) & 0x1);
        
        if(TableU.find(MatchB) != TableU.end()) {
            //cout << "Find the Match!\n";
            vector<UINT64> ttmp = TableU[MatchB];
            MatchNum += ttmp.size();
            for (UINT64 k=0; k<ttmp.size(); k++) {
                if(((ttmp[k]<<8)^j)==rightkey)
                    cout << "The original values for red and blue cells remain!" << endl;
            }
        }
    }
    
    //cout << "In total, " << MatchNum << " matches are found!" << endl;
    cout << "In total, 2^" << log(double(MatchNum))/log(2.0) << " matches are found!" << endl;
    return 0;

}


