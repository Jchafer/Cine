import java.util.ArrayList;


public class Sales {

private static ArrayList<Sala> sales;

	
	
	//CONSTRUCTORS
	public Sales() {
		this.sales = new ArrayList<Sala>();
	}
	
	public Sales(ArrayList<Sala> sales) {
		this.sales = sales;
	}
	//METODES
	public static void afegirSala(Sala s){
		sales.add(s);
	}
	
	public static void llistarSales(){
		if (sales.size()==0) System.out.println("\n\t No hi ha cap SALA registrada");
		for(int i=1; i<=sales.size();i++){
			
			System.out.print("\n\t "+i+": "+sales.get(i-1).toString());
		}
			
	}
	
	public static boolean validaIdSala(int s){
		boolean resultat = true;
		for(int i=0; i<sales.size();i++){
			if (sales.get(i).getNumeroSala()==s) return false;
		}
		return resultat;
	}
	

	public static Sala retornaSala(int i){
		if (i <= sales.size())
			return sales.get(i-1);
		else {
			System.out.println("ERROR Sales.retornaSala: valor proporcionat fora de rang");
			return null;
		}
		
	}
	
	public static void modificaSala(int i){
		if (i <= sales.size())
			sales.get(i-1).modificaSala();
		else {
			System.out.println("ERROR Sales.modificaSala: valor proporcionat fora de rang");
		}
	}
	
	public static void esborraSala(int i){
		if (i <= sales.size()){
			
			sales.get(i-1).esborraSala();
			sales.remove(i-1);
		}else {
			System.out.println("ERROR Sales.modificaSala: valor proporcionat fora de rang");
		}
	}
	
	public static int quantitatSales(){
		return sales.size();
	}
	
	//GETTERS & SETTERS
	public  ArrayList<Sala> getSales() {
		return sales;
	}

	public  void setSales(ArrayList<Sala> sales) {
		this.sales = sales;
	}
	
}
