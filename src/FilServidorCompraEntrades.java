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
		int intPelicula, intSesion;
		Pelicula pelicula;
		Sessio sesion;
		Sala sala;

		try {
			// Input socket creation
			InputStream in = socket.getInputStream();
			// getting data from Client socket
			DataInputStream inStream = new DataInputStream(in);

			// OutputStream creation
			OutputStream out = socket.getOutputStream();
			// setting a new DataOutputStream to send data to Client
			DataOutputStream outStream = new DataOutputStream(out);
			
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

			System.out.println("["+this.getName()+"] Receiving film selection from Client ");
			intPelicula = Integer.valueOf(inStream.readUTF());
			pelicula = Pelicules.retornaPelicula(intPelicula);
			if(pelicula == null) {
				outStream.writeUTF("ERROR");
				closeSocket(socket);
				return;
			}
			
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
			
			System.out.println("["+this.getName()+"] Receiving session selection from Client ");
			intSesion = Integer.valueOf(inStream.readUTF());
			sesion = pelicula.retornaSessioPeli(intSesion);
			if(sesion == null) {
				outStream.writeUTF("ERROR");
				closeSocket(socket);
				return;
			}
			
			System.out.println("["+this.getName()+"] Sending the session map to Client ");
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

}