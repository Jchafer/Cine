import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class FilServidorCompraEntrades extends Thread{
	private Socket socket;
	private String listaPeliculas = "Lista de películas: ";
	private String listaSesiones = "Lista de sesiones: ";

	//CONSTRUCTOR
	public FilServidorCompraEntrades(Socket socket, int i) {
		this.socket = socket;
		this.setName("ServidorCompraEntradesClient "+i);
	}

	public void run() {
		int intPelicula, intSesion, cantidadTiquets;
		Pelicula pelicula;
		Sessio sesion;
		Sala sala;
		String mapa;
		boolean entradasCompradas = false;

		try {
			// Input socket creation
			InputStream in = socket.getInputStream();
			// getting data from Client socket
			DataInputStream inStream = new DataInputStream(in);

			// OutputStream creation
			OutputStream out = socket.getOutputStream();
			// setting a new DataOutputStream to send data to Client
			DataOutputStream outStream = new DataOutputStream(out);
			
			// Enviar lista de películas
			System.out.println("["+this.getName()+"] Sending list of films to Client ");			
			if (Pelicules.llistarPelicules() == 0) {
				System.out.println("\t ERROR Cine:compraEntradaPelicula: No hi ha PELICULES");
				outStream.writeUTF("ERROR");
				closeSocket(socket);
				return;
			}
			for(int i=1; i<=Pelicules.quantitatPelicules();i++){
				listaPeliculas += "\n\t "+i+"-> "+Pelicules.getPelicules().get(i-1).toString();
			}			
			outStream.writeUTF(listaPeliculas);

			// Recibir película elegida
			System.out.println("["+this.getName()+"] Receiving film selection from Client ");
			intPelicula = Integer.valueOf(inStream.readUTF());
			pelicula = Pelicules.retornaPelicula(intPelicula);
			if(pelicula == null) {
				outStream.writeUTF("ERROR");
				closeSocket(socket);
				return;
			}
			
			// Enviar lista de sesiones
			System.out.println("["+this.getName()+"] Sending sessions list for the film to Client ");
			if (pelicula.llistarSessionsPeli()== 0) {
				System.out.println("\t ERROR Cine:compraEntradaPelicula: No hi ha SESSIONS per a esta PELICULA");
				outStream.writeUTF("ERROR");
				closeSocket(socket);
				return;
			}
			for(int i=1; i<=pelicula.llistarSessionsPeli();i++){
				listaSesiones += "\n\t "+i+"-> "+pelicula.getSessionsPeli().get(i-1).toString();
			}			
			outStream.writeUTF(listaSesiones);
			
			// Recibir sesión elegida
			System.out.println("["+this.getName()+"] Receiving session selection from Client ");
			intSesion = Integer.valueOf(inStream.readUTF());
			sesion = pelicula.retornaSessioPeli(intSesion);
			sala = sesion.getSala();
			if(sesion == null) {
				outStream.writeUTF("ERROR");
				closeSocket(socket);
				return;
			}
			
			// Enviar mapa de la sesión
			System.out.println("["+this.getName()+"] Sending the session map to Client ");
			mapa = sesion.mapaSessioString();
			outStream.writeUTF(mapa + "\nCantidad de tiquets a comprar: ");
			
			// Recibir cantidad tiquets
			System.out.println("["+this.getName()+"] Receiving number of tickets to buy from ");
			cantidadTiquets = Integer.valueOf(inStream.readUTF());
			
			Seient[][] seients = sesion.getSeients();
			
			//Selecció dels seients en funció del num entrades a comprar
			SeientsReservats seientsAreservar = new SeientsReservats();
			
			System.out.println("["+this.getName()+"] Receiving seats ");
			for (int i=0; i < cantidadTiquets; i++){
				outStream.writeUTF("\tSeient "+(i+1)+" :\n\t\t Tria FILA: [1-"+sala.getFiles()+"]");
				int numFila = Integer.valueOf(inStream.readUTF());
				outStream.writeUTF("\tSeient "+(i+1)+" :\n\t\t Tria COLUMNA: [1-"+sala.getTamanyFila()+"]");
				int numColumna = Integer.valueOf(inStream.readUTF());
				seientsAreservar.afegirSeient(seients[numFila-1][numColumna-1]);
			}
			
			try {
				entradasCompradas = reservaSeientsInteractiu(pelicula, sesion, sala, cantidadTiquets, seientsAreservar);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (entradasCompradas) {
				outStream.writeUTF("Paying accepted from Client");
			}else {
				outStream.writeUTF("You couldn’t buy " + cantidadTiquets + " tickets from Client. Please try it again in a few minutes.");
			}
			closeSocket(socket);
			return;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeSocket(Socket socket) throws IOException {
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

	public boolean reservaSeientsInteractiu(Pelicula p, Sessio se, Sala sa, int numEntrades, SeientsReservats seientsAcomprar) throws InterruptedException {
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
					/*System.out
					.println("["
							+ this.nom
							+ "]\t ERROR Cine:validaSeient: Seient reservat/ocupat");*/
				}// else
			}
			// ////////////////////////////////////////////////////////////////////////////
			// END SYNCHRONIZED
			// ////////////////////////////////////////////////////////////////////////////
		}// for
		//Ja no ens fan falta el llistat de seients que voliem reservar
		seientsAcomprar=null;

		if (!isreservat) {// Llibera seients
			/*System.out.println("[" + "["+this.nom+"]"
					+ "]\tNO sha pogut fer la compra de " + numEntrades
					+ " entrades");*/
			for (int i = 0; i < seientsreservats.quantitatSeientsReservats(); i++) {
				Seient asiento = seientsreservats.retornaSeient(i);
				asiento.alliberaSeient(); // ocupa seient
			}// for
		}// if
		for (int i = seientsreservats.quantitatSeientsReservats(); i > 0; i--) 
			seientsreservats.esborraSeient(i - 1); // elimina seient de la
		// llista
		return isreservat;
	}
	
}