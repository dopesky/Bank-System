/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bank;

import java.util.*;
import java.io.*;

/**
 *
 * @author Kevin
 */
public class BankConsole {
    public static ArrayList<ArrayList<String>> data=getFile();
    static Thread hook=new Thread(){
            @Override
            public void run(){
                try{
                    PrintWriter temp=new PrintWriter("AccountData.txt");
                    for(int i=0;i<data.size();i++){
                        for(int j=0;j<data.get(i).size();j++){
                            temp.write(data.get(i).get(j)+"//");
                        }
                        temp.println();
                    }
                    temp.close();
                }catch(Exception e){}
            }
        };
   
    public static int checkInt(Scanner e){
        int value;
        do{
            while(!e.hasNextInt()){
                System.out.print("Please use Numbers for this data.\nInput: ");
                e.next();
            }
            value=e.nextInt();
            if(value<=0){
                System.out.print("Use Numbers greater than 0.\nInput: ");
            }
        }while(value<=0);
        return value;
    }
    
    public static ArrayList<ArrayList<String>> getFile(){
        ArrayList<ArrayList<String>> data=new ArrayList<>();
        try{
            Scanner in=new Scanner(new FileInputStream("AccountData.txt")).useDelimiter("\n");
            while(in.hasNext()){
                ArrayList<String> temp=new ArrayList<>();
                String next=in.next().trim();
                Scanner inner=new Scanner(next).useDelimiter("//");
                while(inner.hasNext()){
                    String Inner=inner.next().trim();
                    temp.add(Inner);
                }
                data.add(temp);
            }
        }catch(FileNotFoundException fnfe){}
        return data;
    }
    
    public static int createAccount(ArrayList<ArrayList<String>> e){
        Scanner in=new Scanner(System.in);
        System.out.print("Input Acc.No: ");
        int account=checkInt(in);
        while(String.valueOf(account).length()!=6){
            System.out.print("Account Numbers must be 6 digits long.\nInput Acc.No: ");
            account=checkInt(in);
        }
        for(int i=0;i<e.size();i++){
            if(String.valueOf(account).equals(e.get(i).get(0))){
                System.out.println("Account Number already in use.");
                return 1;
            }
        }
        System.out.print("Input Name: ");
        String name=in.next();
        System.out.print("Input Acc. Type: ");
        String type=in.next();
        int pin,pin2;
        do{
            System.out.print("Input Pin: ");
            pin=checkInt(in);
            System.out.print("Repeat Pin: ");
            pin2=checkInt(in);
            if(pin!=pin2){
                System.out.println("Pins do not match.\n");
            }
        }while(pin!=pin2);
        ArrayList<String> temp=new ArrayList<>();
        temp.add(String.valueOf(account));
        temp.add(name);
        temp.add(type);
        temp.add("0");
        temp.add(String.valueOf(pin));
        e.add(temp);
        return 0;
    }
    
    public static void deposit(ArrayList<ArrayList<String>> e,Scanner j,int num){
        System.out.print("Enter Amount to deposit: ");
        int amount=checkInt(j);
        for(int i=0;i<e.size();i++){
            if(e.get(i).get(0).equalsIgnoreCase(String.valueOf(num))){
                int before=Integer.parseInt(e.get(i).get(3));
                int balance=before+amount;
                e.get(i).set(3,String.valueOf(balance));
            }
        }
    }
    
    public static int withdraw(ArrayList<ArrayList<String>> e,Scanner j,int num){
        System.out.print("Enter Amount to withdraw: ");
        int amount=checkInt(j);
        for(int i=0;i<e.size();i++){
            if(e.get(i).get(0).equalsIgnoreCase(String.valueOf(num))){
                int before=Integer.parseInt(e.get(i).get(3));
                int balance=before-amount;
                if(balance<0){
                    System.out.println("Withdrawal denied! Not enough funds!");
                    return 1;
                }
                e.get(i).set(3,String.valueOf(balance));
            }
        }
        return 0;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(hook);
        while (true){
        Scanner in=new Scanner(System.in);
        System.out.print("\n\n\u001B[1mKey:\u001B[0m \n1.Login\n2.Create New Account\n3.Exit\n\nEnter number: ");
        int ans;
        do{
            ans=checkInt(in);
            if(ans>3){
                System.out.print("Use the key to write input.\nEnter number: ");
            }
        }while(ans>3);
        if(ans==2){
            while(createAccount(data)==1){}
            continue;
        }
        if(ans==3){
            System.exit(0);
        }
        System.out.print("Input Acc No: ");
        int one=checkInt(in);
        while(String.valueOf(one).length()!=6){
            System.out.print("Account Number must be 6 numbers.\nInput Acc No: ");
            one=checkInt(in);
        }
        System.out.print("Enter Pin: ");
        int two=checkInt(in);
        boolean viable=false;
        for(int i=0;i<data.size();i++){
            if(data.get(i).get(0).equalsIgnoreCase(String.valueOf(one))&&data.get(i).get(4).equalsIgnoreCase(String.valueOf(two))){
                viable=true;
            }
        }
        if(!viable){
            System.out.println("\n\n********************************\nLogin Unsuccessful\nInvalid Credentials\n********************************\n\n");
            continue;
        }
        System.out.print("\n\n\u001B[1mKey:\u001B[0m \n1.Deposit\n2.Withdraw\n3.Exit\n\nEnter number:");
        do{
            ans=checkInt(in);
            if(ans>3){
                System.out.print("Use the key to write input.\nEnter number: ");
            }
        }while(ans>3);
        switch(ans){
            case(1):
                deposit(data,in,one);
                break;
            case(2):
                while(withdraw(data,in,one)==1){}
                break;
            case(3):
                System.exit(0);
                break;
        }
    }
    }
}
