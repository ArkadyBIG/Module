import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class FreeCashier {
	public static void main(String[] args) throws InterruptedException{
		List<Cashier> cashiers = new ArrayList<>();
		cashiers.add(new Cashier());
		cashiers.add(new Cashier());
		cashiers.add(new Cashier());
		
		List<Customer> customers = new ArrayList<>();
		for (Integer i = 0; i < 10; i++)
		{
			Customer cust = new Customer(cashiers, i);
			cust.start();
			customers.add(cust);
		}
		for (Cashier cash : cashiers)
			cash.start();
		for (Customer cust : customers)
		{
			cust.join();
		}

	}

	static class Customer extends Thread{
		public List<Cashier> cashiers;
		public Boolean is_with_cashier = false;
		public Boolean finished = false;
		public Integer cashier_index = 0;
		public Integer id;

		Customer(List<Cashier> cashiers, Integer id){
			this.id = id;
			this.cashiers = cashiers;
			cashiers.get(cashier_index).addCustomer(this);
		}

		public void run()  {
			while (!finished && !is_with_cashier){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}

				Integer line_num = cashiers.get(cashier_index).line.indexOf(this);

				if (line_num == -1)
					break;

				for (Cashier cash : cashiers) {
					if (cash == this.cashiers.get(cashier_index))
						continue;
					
					if (line_num - cash.lineSize() > 5){
						cashiers.get(cashier_index).removeCustomer(this);
						cashier_index = cashiers.indexOf(cash);
						System.out.println("ID: " + id.toString() + ". Switching to " + cashier_index.toString());
						cashiers.get(cashier_index).addCustomer(this);
					} 

				} 

			}

			System.out.println("ID: " + id.toString() + ". Have finished.");

		}

	}

	public static class Cashier extends Thread{
		public List<Customer> line;
		Cashier(){
			line = new CopyOnWriteArrayList<Customer>();
		}

		Integer lineSize(){
			return line.size();
		}

		void addCustomer(Customer cust){
			line.add(cust);
		}

		void removeCustomer(Customer cust){
			line.remove(cust);
		}

		public void run() {
			while (true){
				if (!line.isEmpty())
				{
					Customer cur = line.remove(0);
					cur.is_with_cashier = true;
					System.out.println("Working with: " + cur.id.toString());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
					System.out.println("Finished with: " + cur.id.toString());
					cur.finished = true;
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
		}

	}
}
