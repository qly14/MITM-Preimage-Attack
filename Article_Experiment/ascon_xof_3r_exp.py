#
#  3r experiments on Ascon-XOF
#  
#  Created on 2022/9/29.
#

import sympy as sy
import operator
import math
import copy
import random


red=sy.symbols('red')
blue=sy.symbols('blue')

def R_rot(A,k):  
    k1 = k%64
    B=[]
    for i in range(k1):
        B = B+[A[64-k1+i]]
    for i in range(64-k1):
        B = B+[A[i]]
    return B 

def hextobin(x,l):
    m=[]
    while x>0:
        m=m+[x%2]
        x=x//2
    while len(m)<l:
        m.append(0)
    n=m[::-1]
    return n
    
def inistate(a,cd): 
    l=len(cd)
    b=[]
    #y=[0 for i in range(len(a))]
    for i in range(2**l):
        n=hextobin(i,l)
        y=copy.deepcopy(a)
        for j in range(l):
            y[cd[j]]=n[j]
        b=b+[y]
    return b
            
        
def linear(a):
    b0=[0 for i in range(64)]
    b1=[0 for i in range(64)]
    b2=[0 for i in range(64)]
    b3=[0 for i in range(64)]
    b4=[0 for i in range(64)]
    
    for i in range(64):
        b0[i]=(a[i]+R_rot(a[0:64],19)[i]+R_rot(a[0:64],28)[i])%2
        b1[i]=(a[64+i]+R_rot(a[64:128],61)[i]+R_rot(a[64:128],39)[i])%2
        b2[i]=(a[128+i]+R_rot(a[128:192],1)[i]+R_rot(a[128:192],6)[i])%2
        b3[i]=(a[192+i]+R_rot(a[192:256],10)[i]+R_rot(a[192:256],17)[i])%2
        b4[i]=(a[256+i]+R_rot(a[256:320],7)[i]+R_rot(a[256:320],41)[i])%2  
        
    return b0+b1+b2+b3+b4    
  
    
def Addcons(a,k):
    c=[0x00000000000000f0,0x00000000000000e1,0x00000000000000d2,0x00000000000000c3,0x00000000000000b4,0x00000000000000a5,\
       0x0000000000000096,0x0000000000000087,0x0000000000000078,0x0000000000000069,0x000000000000005a,0x000000000000004b]
  
    b=bin(c[k])[2:]
    d=[]
    for i in range(len(b)):
        d=d+[int(b[i])]
    
    
    y=copy.deepcopy(a)
    while len(d)<64:
        d=[0]+d  
    for i in range(64):
        y[128+i]=(y[128+i]+d[i])%2
       
    return y

def Sbox_cond(a,k):
    b0=[0 for i in range(64)]
    b1=[0 for i in range(64)]
    b2=[0 for i in range(64)]
    b3=[0 for i in range(64)]
    b4=[0 for i in range(64)]
    for i in range(64):
        b0[i]=(a[256+i]*a[64+i]+a[192+i]+a[128+i]*a[64+i]+a[128+i]+a[64+i]*a[i]+a[64+i]+a[i])%2
        b1[i]=(a[256+i]+a[192+i]*a[128+i]+a[192+i]*a[64+i]+a[192+i]+a[128+i]*a[64+i]+a[128+i]+a[64+i]+a[i])%2        
        b2[i]=(a[256+i]*a[192+i]+a[256+i]+a[128+i]+a[64+i]+1)%2
        b3[i]=(a[256+i]*a[i]+a[256+i]+a[192+i]*a[i]+a[192+i]+a[128+i]+a[64+i]+a[i])%2
        b4[i]=(a[256+i]*a[64+i]+a[256+i]+a[192+i]+a[64+i]*a[i]+a[64+i])%2

        
    c=b0+b1+b2+b3+b4   
    if k==0:
        for i in range(320):
            if c[i]==(red+red)%2 or c[i]==(red+red+red)%2 :
                c[i]=0
            elif c[i]==(red+red+1)%2 or c[i]==(red+red+red+1)%2:
                c[i]=1
    elif k==1:
        for i in range(320):
            if c[i]==(blue+blue)%2 or c[i]==(blue+blue+blue)%2:
                c[i]=0
            elif c[i]==(blue+blue+1)%2 or c[i]==(blue+blue+blue+1)%2:
                c[i]=1    
    
    return c


def Sbox(a):
    b0=[0 for i in range(64)]
    b1=[0 for i in range(64)]
    b2=[0 for i in range(64)]
    b3=[0 for i in range(64)]
    b4=[0 for i in range(64)]
    for i in range(64):
        b0[i]=(a[256+i]*a[64+i]+a[192+i]+a[128+i]*a[64+i]+a[128+i]+a[64+i]*a[i]+a[64+i]+a[i])%2
        b1[i]=(a[256+i]+a[192+i]*a[128+i]+a[192+i]*a[64+i]+a[192+i]+a[128+i]*a[64+i]+a[128+i]+a[64+i]+a[i])%2        
        b2[i]=(a[256+i]*a[192+i]+a[256+i]+a[128+i]+a[64+i]+1)%2
        b3[i]=(a[256+i]*a[i]+a[256+i]+a[192+i]*a[i]+a[192+i]+a[128+i]+a[64+i]+a[i])%2
        b4[i]=(a[256+i]*a[64+i]+a[256+i]+a[192+i]+a[64+i]*a[i]+a[64+i])%2
       
    c=b0+b1+b2+b3+b4  
    return c
    
 
def compute_redsolution(a,index_red):
    red_solution_table=[[] for i in range(2**2)]  
    
    red_solution=inistate(a,index_red[0:7])
    
    for i in range(2**7):
        
        b0=Addcons(red_solution[i],0)
        
        c0=Sbox(b0)
              
        b1=linear(c0)                 
        
        b1=Addcons(b1,1)
        
        c1=Sbox(b1)       
        
        b2=linear(c1)
        
        b2=Addcons(b2,2)  

        ind=2* b2[71]+b2[103]

        red_solution_table[ind]=red_solution_table[ind]+[red_solution[i]]
        
    return red_solution_table


def compute_value(a):

    b0=Addcons(a,0)    
    c0=Sbox(b0)          
    b1=linear(c0)                 
    
    b1=Addcons(b1,1)    
    c1=Sbox(b1)           
    b2=linear(c1)
    
    b2=Addcons(b2,2)
    red_num=[b2[71],b2[103]]  
    gray_value=[b2[64+4],b2[64+7],b2[64+36],b2[64+39]]
    c2=Sbox(b2)
    
    v=[c2[4],c2[7],c2[36],c2[39],c2[43]]
    
    return v+red_num+gray_value
    

def change(a,num):
    for i in range(num):
        if a[i]!=0 and a[i]!=1:
            a[i]=red 
    return a

def change_blue(a,num):
    for i in range(num):
        if a[i]!=0 and a[i]!=1:
            a[i]=blue
    return a


    

if __name__== "__main__":
       
    
    index_red=[5, 8, 11, 13, 16, 35, 57]
    index_blue=[19, 26, 28, 31, 50]
    
    
    cond_index=[64+5,64+8,192+8,64+11,192+11,64+13,192+13,\
                64+16,192+16,64+19,192+19,64+26,192+26,\
                64+28,64+31,64+35,64+50,192+50,64+57,192+57,\
                256+8,256+11,256+13,256+16,256+19,256+26,256+50,256+57]
    cond=[1,1,1,1,1,1,1,\
          0,1,0,1,1,1,\
          0,0,1,1,1,1,1,\
          0,0,0,0,0,0,0,0]
        
    
    for ii in range(2**6):
               
        ini=[]
        for i in range(320):
           ini=ini+[random.randint(0,1)]   #random choose initial state 
        
        for i in range(len(cond)):   #add conditions
            ini[cond_index[i]]=cond[i]   

        #print(ini)
            
            
        match_value=compute_value(ini)  

        red_num=[match_value[5],match_value[6]]
                         
        blue_solution=inistate(ini,index_blue[0:5])

        red_solution=compute_redsolution(ini,index_red[0:7])  #build table of red solutions
           
        blue_table=[[] for i in range(2**5)]    
        
        for kk in range(2**5):
            
            a=blue_solution[kk]            
            
            blue_value= a[index_blue[0]]*2**4+a[index_blue[1]]*2**3+a[index_blue[2]]*2**2+a[index_blue[3]]*2+a[index_blue[4]]       
            
        
            for i in range(len(index_red)):
                a[index_red[i]]=red
                  
                
            b0=Addcons(a,0)
            b0=change(b0,320)
            
            c0=Sbox_cond(b0,0)
            c0=change(c0,320)
            
            b1=linear(c0)
            b1=change(b1,320)                   
            
            b1=Addcons(b1,1)
            b1=change(b1,320)
            
            c1=Sbox(b1)
            c1=change(c1,320)
            
            
            b2=linear(c1)
            b2=change(b2,320) 
            
            cd1=[71,103]
            b2[71]=red_num[0]
            b2[103]=red_num[1]
            
    
            b2=Addcons(b2,2)
            b2=change(b2,320)
                
            
            B1=(b2[256+4]*match_value[7]+b2[192+4]+b2[128+4]*match_value[7]+b2[128+4]+match_value[0])%2
            B2=(b2[128+7]*match_value[8]+b2[128+7]+match_value[1])%2
            B3=(b2[256+36]*match_value[9]+b2[192+36]+match_value[2])%2
            B4=(b2[128+39]*match_value[10]+b2[128+39]+match_value[3])%2
            B5=(b2[192+43]+match_value[4])%2
        
            value=B1*2**4+B2*2**3+B3*2**2+B4*2+B5            
            
            blue_table[value]=blue_table[value]+[blue_value]                          
        
        
        red_table=[[] for i in range(2**5)] 

        ll=len(red_solution[2*red_num[0]+red_num[1]])
        
        for kk in range(ll):
            
            a=red_solution[2*red_num[0]+red_num[1]][kk]                       
            
            red_value= a[index_red[0]]*2**6+a[index_red[1]]*2**5+a[index_red[2]]*2**4+a[index_red[3]]*2**3+a[index_red[4]]*2**2+a[index_red[5]]*2+a[index_red[6]]        
            
        
            for i in range(len(index_blue)):
                a[index_blue[i]]=blue
                
            b0=Addcons(a,0)
            b0=change_blue(b0,320)
            
            c0=Sbox_cond(b0,1)
            c0=change_blue(c0,320)
            
            
            b1=linear(c0)
            b1=change_blue(b1,320)
                        
            b1=Addcons(b1,1)
            b1=change_blue(b1,320)
            
            c1=Sbox(b1)
            c1=change_blue(c1,320)
            
            b2=linear(c1)
            b2=change_blue(b2,320)  
    
            b2=Addcons(b2,2)
            b2=change_blue(b2,320)
                       
            
            R1=(b2[4]*match_value[7]+b2[4]+match_value[7])%2
            R2=(b2[256+7]*match_value[8]+b2[192+7]+match_value[8]*b2[7]+match_value[8]+b2[7])%2
            R3=(b2[128+36]*match_value[9]+b2[128+36]+match_value[9]*b2[36]+match_value[9]+b2[36])%2
            R4=(b2[256+39]*match_value[10]+b2[192+39]+match_value[10]*b2[39]+match_value[10]+b2[39])%2
            R5=(b2[256+43]*b2[64+43]+b2[128+43]*b2[64+43]+b2[128+43]+b2[64+43]*b2[43]+b2[64+43]+b2[43])%2
            
            value= R1*2**4+R2*2**3+R3*2**2+R4*2+R5
            
            
            red_table[value]=red_table[value]+[red_value]
                  
    
        mat_num=0
        for i in range(2**5):
            if len(blue_table[i])!=0 and len(red_table[i])!=0:
                mat_num=mat_num+ len(blue_table[i])* len(red_table[i])
        
        print('matching pairs are:',mat_num)
    
    





 



