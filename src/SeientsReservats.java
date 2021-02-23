import java.util.ArrayList;


public class SeientsReservats {
	//ATRIBUTS
	private ArrayList<Seient> seientsreservats;
	

	//CONSTRUCTORS
//*********************************************************
	public SeientsReservats() {
		this.seientsreservats = new ArrayList<Seient>();
	}
	
	// ---------------------------------
	public SeientsReservats(ArrayList<Seient> seientsReservats) {
		this.seientsreservats = seientsReservats;
	}
	
	//METODES
	//*********************************************************
	public void afegirSeient(Seient s){
		seientsreservats.add(s);
	}

	// ---------------------------------
	public Seient retornaSeient(int i){
		if (i <= seientsreservats.size())
			return seientsreservats.get(i);
		else {
			System.out.println("ERROR SeientsReservats.retornaSeient: valor proporcionat fora de rang");
			return null;
		}	
	}
	// ---------------------------------
	
	public void esborraSeient(int i){
		if (i <= seientsreservats.size()){		
			seientsreservats.remove(i);
		}else {
			System.out.println("ERROR SeientsReservats.modificaSeient: valor proporcionat fora de rang");
		}
	}
	// ---------------------------------	

	public int quantitatSeientsReservats(){
		return seientsreservats.size();
	}
	
	// ---------------------------------
	//GETTERS & SETTERS
	public  ArrayList<Seient> getSeientsReservats() {
		return seientsreservats;
	}

	// ---------------------------------
	public  void setSeientsReservats(ArrayList<Seient> seientsReservats) {
		this.seientsreservats = seientsReservats;
	}
	
}
