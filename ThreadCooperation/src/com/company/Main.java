package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static Account account = new Account();

    public static void main(String[] args) {
	// write your code here
        // kreiranje pula niti sa dve niti
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new DepositTask());
        executor.execute(new WithdrawTask());
        executor.shutdown();
        System.out.println("Thread 1\t\t Thread 2\t\t Balance");
    }
    public static class DepositTask implements Runnable{
         // nastavak dodavanja novca na racun

        @Override
        public void run() {
            try{ // namerno odlaganje da bi se dozvolilo zadatku povecanje da radi
                while(true){
                    account.deposit((int)(Math.random()*10) +1);
                    Thread.sleep(1000);
                }

            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }

        }
    }
    public static class WithdrawTask implements Runnable{
        @Override // oduzimanje novca sa racuna
        public void run() {
            while (true){
                account.withdraw((int)(Math.random()*10) + 1);
            }

        }
    }
    // An inner class for account
    private static class Account{
        // kreiranje novog kljuca
        private static Lock lock = new ReentrantLock();

        // kreiranje uslova
        private static Condition newDeposit = lock.newCondition();

        private int balance = 0;

        public  int getBalance(){
            return balance;
        }
        public void withdraw(int amount){
            lock.lock(); // trazenje kljuca
            try{
                while (balance<amount){
                    System.out.println("\t\t\tWait for deposit");
                    newDeposit.await();
                }
                balance -=amount;
                System.out.println("\t\t\tWithdraw " + amount + "\t\t\t" + getBalance());
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
            finally {
                lock.unlock(); // oslobadjanje kljuca
            }
        }
        public void deposit(int amount){
            lock.lock(); // trazenje kljuca
            try{

            balance +=amount;
            System.out.println("Deposit " + amount + "\t\t\t\t\t" + getBalance());
            // Signal thread waiting on the condition
            newDeposit.signalAll();
        }
            finally {
                lock.unlock(); // oslobadjanje kljuca
            }
            }

    }
    
}
