package main

import (
	"fmt"
	"sync"
	"time"
	"math/rand"
)
type Balance struct {
	mu sync.Mutex
	balance int
}

var bank_balance = Balance{balance: 100000}

func keepping_bank_balance_in_range(){
	for {
		time.Sleep(time.Second)
		bank_balance.mu.Lock()
		if bank_balance.balance < 50000 {
			bank_balance.balance += 100000 // From storage
		}
		if bank_balance.balance > 150000 {
			bank_balance.balance -= 20000 // To storage
		}
		bank_balance.mu.Unlock()
		fmt.Println("Bank balance: ", bank_balance.balance)
	}
}

func customer_action(){
	for i := 0; i < 10; i++ {
		bank_balance.mu.Lock()
		switch rand.Intn(4) {
			case 0:
				bank_balance.balance += rand.Intn(10000);
			case 1:
				bank_balance.balance -= rand.Intn(10000);
			case 2:
				bank_balance.balance += rand.Intn(30000);
			case 3:
				bank_balance.balance -= rand.Intn(40000);
		}
		bank_balance.mu.Unlock()
		time.Sleep(time.Millisecond * 500)
	}
}

func main() {
    go keepping_bank_balance_in_range()
	fmt.Println("hello world")
	for i := 0; i < 100; i++ {
		customer_action()
		time.Sleep(time.Millisecond * 500)
	}
}
