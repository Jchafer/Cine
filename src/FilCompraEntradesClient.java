import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class FilCompraEntradesClient {

	//ATRIBUTS
	private static String nom;
	private Pelicula pelicula;
	private Sessio sessio;
	private int numEntrades;
	private SeientsReservats seientsAreservar;
	
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) throws UnknownHostException {
		
		InetAddress IPdeServer = InetAddress.getLocalHost();
		int portDeServer = 9000;
		String stringAEnviar, stringRecibido;
		try {
			Socket socketDeServer = new Socket(IPdeServer, portDeServer);
			System.out.println("CLIENTE Puerto del servidor " + portDeServer);
			
			// Recibe lista peliculas
			stringRecibido = recibirDatos(socketDeServer);			
			System.out.println("[Servidor] " + stringRecibido);
			
			// Enviar pelicula elegida
			enviarDatos(socketDeServer);
			
			// Recibir lista sesiones
			stringRecibido = recibirDatos(socketDeServer);
			System.out.println("[Servidor] " + stringRecibido);
			
			// Enviar sesion elegida
			enviarDatos(socketDeServer);
			
			// Recibir mapa
			stringRecibido = recibirDatos(socketDeServer);
			System.out.println("[Servidor] " + stringRecibido);
			
			// Enviar cantidad tiquets
			int cantidadTiquets = Integer.valueOf(enviarDatos(socketDeServer));
			
			// Recibir peticiones asientos
			for (int i=0; i < cantidadTiquets; i++){
				stringRecibido = recibirDatos(socketDeServer);
				System.out.println("[Servidor] " + stringRecibido);
				enviarDatos(socketDeServer);
				stringRecibido = recibirDatos(socketDeServer);
				System.out.println("[Servidor] " + stringRecibido);
				enviarDatos(socketDeServer);
			}
			
			// Recibir respuesta
			stringRecibido = recibirDatos(socketDeServer);
			System.out.println("[Servidor] " + stringRecibido);
						
			closeSocket(socketDeServer);
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String enviarDatos(Socket socketDeServer) throws IOException {
		OutputStream out = socketDeServer.getOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(out);
		
		String stringAEnviar = sc.nextLine();
		dataOutput.writeUTF(stringAEnviar);
		
		return stringAEnviar;
	}
	
	public static String recibirDatos(Socket socketDeServer) throws IOException {
		InputStream in = socketDeServer.getInputStream();
		DataInputStream dataInput = new DataInputStream(in);
		String stringRecibido = dataInput.readUTF();
		return stringRecibido;
	}
	
	public static void closeSocket(Socket socket) throws IOException {
		if (socket != null && !socket.isClosed()) {
			if (!socket.isInputShutdown()) {
				socket.shutdownInput();
			}
			if (!socket.isOutputShutdown()) {
				socket.shutdownOutput();
			}
			socket.close();
		}
	}
	
	//METODES
	// ----------
	/*public void run() {		
		
		System.out.println("\n----------------------\n\nInici del fil INTERACTIU"+ "\n----------------------\n");
		//Si NO hi ha PELICULES, s'ix del procés de compra
		if (Pelicules.llistarPelicules() == 0) {
			System.out.println("\t ERROR Cine:compraEntradaPelicula: No hi ha PELICULES");
			return;
		}
		
		//Selecció de PELICULA
		int intPelicula = Validacio.validaSencer("\t Tria PELICULA:",Pelicules.quantitatPelicules());
		this.pelicula = Pelicules.retornaPelicula(intPelicula);
		System.out.println(this.pelicula);
		System.out.println();
		System.out.println();
		
		//Si NO hi ha SESSIONS per la PELICULA, s'ix del procés de compra
		if (this.pelicula.llistarSessionsPeli()== 0) {
			System.out.println("\t ERROR Cine:compraEntradaPelicula: No hi ha SESSIONS per a esta PELICULA");
			return;
		}
		//Selecció de la SESSIO
		int intSessio = Validacio.validaSencer("\t Tria la sessió per a "+this.pelicula.getNomPeli()+":",this.pelicula.getSessionsPeli().size());

		this.sessio = this.pelicula.retornaSessioPeli(intSessio);
		Sala sala = this.sessio.getSala();
		this.sessio.mapaSessio();
		
		//Selecció de QUANTES entrades es volen comprar
		this.numEntrades = Validacio.validaSencer("\t  Quantes ENTRADES vols comprar? ",sala.getFiles()*sala.getTamanyFila());
		Seient[][] seients = this.sessio.getSeients();
		
		//Selecció dels seients en funció del num entrades a comprar
		SeientsReservats seientsAreservar = new SeientsReservats();
		
		for (int i=0; i < numEntrades; i++){
			System.out.println("\tSeient "+(i+1)+" :");
			int fila = Validacio.validaSencer("\t\t Tria FILA: [1-"+sala.getFiles()+"] ",sala.getFiles());
			int seient = Validacio.validaSencer("\t\t Tria SEIENT en la fila: [1-"+sala.getTamanyFila()+"]",sala.getTamanyFila());		
			//Seient[][] seients = se.getSeients();
			
			//seients[fila-1][seient-1].reservantSeient();	
			seientsAreservar.afegirSeient(seients[fila-1][seient-1]);//afegeix seient a llista SEIENTS RESERVATS
			
			if ((i+1)!=numEntrades)
			System.out.println("\t Següent Seient...");
		}//endfor
		
		
		try {
			reservaSeientsInteractiu(pelicula, sessio, sala, numEntrades, seientsAreservar);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sessio.mapaSessio();

	}*/


	// ----------------------------------------
	public void reservaSeientsInteractiu(Pelicula p, Sessio se, Sala sa, int numEntrades, SeientsReservats seientsAcomprar) throws InterruptedException {
		boolean isreservat = true, ispagat = false;

		//arraylist temporal on desarem els seients realment reservats
		SeientsReservats seientsreservats = new SeientsReservats();

		for (int i = 0; i < numEntrades; i++) {

			//llistat de Seients de la Sessio
			Seient[][] seients = se.getSeients();

			Seient seientAcomprar = seientsAcomprar.retornaSeient(i);

			// ///////////////////////////////////////////////
			// SINCRONITZACIÓ DE CODI
			// ///////////////////////////////////////////////
			synchronized (this) {

				if (seients[seientAcomprar.getFilaSeient()][seientAcomprar.getNumeroSeient()].verificaSeient()) { // Reserva
					// SEIENT
					seients[seientAcomprar.getFilaSeient()][seientAcomprar.getNumeroSeient()].reservantSeient(); // afegeix
					// seient a llista SEIENTS RESERVATS
					seientsreservats.afegirSeient(seients[seientAcomprar.getFilaSeient()][seientAcomprar.getNumeroSeient()]);
				} else { // NO Reserva
					isreservat = false;
					System.out
					.println("["
							+ this.nom
							+ "]\t ERROR Cine:validaSeient: Seient reservat/ocupat");
				}// else
			}
			// ////////////////////////////////////////////////////////////////////////////
			// END SYNCHRONIZED
			// ////////////////////////////////////////////////////////////////////////////
		}// for
		//Ja no ens fan falta el llistat de seients que voliem reservar
		seientsAcomprar=null;

		if (isreservat) { // Compra seients
			System.out.println("[" + this.nom + "]\t SEIENT OCUPATS: "
					+ seientsreservats.quantitatSeientsReservats());

			ispagat = Cine.pagamentEntrada("["+this.nom+"]", numEntrades, se.getPreu());
			if (ispagat) {
				for (int i = 0; i < seientsreservats.quantitatSeientsReservats(); i++) {
					Seient asiento = seientsreservats.retornaSeient(i);
					asiento.ocupaSeient(); // ocupa seient
					se.imprimirTicket(asiento, se, sa, p);
					System.out.println();
				}// for
			}
		}
		if (!isreservat || !ispagat) {// Llibera seients
			System.out.println("[" + "["+this.nom+"]"
					+ "]\tNO sha pogut fer la compra de " + numEntrades
					+ " entrades");
			for (int i = 0; i < seientsreservats.quantitatSeientsReservats(); i++) {
				Seient asiento = seientsreservats.retornaSeient(i);
				asiento.alliberaSeient(); // ocupa seient
			}// for
		}// if
		for (int i = seientsreservats.quantitatSeientsReservats(); i > 0; i--) 
			seientsreservats.esborraSeient(i - 1); // elimina seient de la
		// llista

	}// function

	
	//GETTERS & SETTERS
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Pelicula getPelicula() {
		return pelicula;
	}

	public void setPelicula(Pelicula pelicula) {
		this.pelicula = pelicula;
	}

	public Sessio getSessio() {
		return sessio;
	}

	public void setSessio(Sessio sessio) {
		this.sessio = sessio;
	}

	public int getNumEntrades() {
		return numEntrades;
	}

	public void setNumEntrades(int numEntrades) {
		this.numEntrades = numEntrades;
	}

	public SeientsReservats getSeientsAreservar() {
		return seientsAreservar;
	}

	public void setSeientsAreservar(SeientsReservats seientsAreservar) {
		this.seientsAreservar = seientsAreservar;
	}


}// class
