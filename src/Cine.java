import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;


public class Cine {

	public static void main(String[] args) throws InterruptedException, IOException {
		int opcio=-1;
		Scanner s = new Scanner(System.in);
		// TODO Auto-generated method stub
		//		String nsala, nsessio, npelicula;
		int sala, sessio, pelicula, numEntrades, i=1; 

		Sales sales = new Sales();
		Sessions sessions = new Sessions();
		Pelicules pelicules = new Pelicules();

		carregaDades(sales, sessions, pelicules);
		
		int puertoEscucha = 9000;
		int contServidor = 0;
		Socket socketClient = null;
		FilServidorCompraEntrades servidor = null;
		
		do{
			opcio = menu();

			switch(opcio){

			case 1: //Crear SALA
				System.out.println("Creant SALA");
				Sala sa = new Sala();
				System.out.println(sa);
				sales.afegirSala(sa);
				break;

			case 2: //Modificar SALA
				System.out.println("Modificant SALA");
				if(Sales.quantitatSales()==0) //NO hi ha sales
					System.out.println("ERROR Modifica SALA: No hi ha sales a modificar");
				else{ //Hi ha sales creades
					Sales.llistarSales();
					sala = Validacio.validaSencer("\t Tria SALA a modificar:",Sales.quantitatSales());
					Sales.modificaSala(sala);
				}
				break;

			case 3: //Esborrar SALA
				System.out.println("Esborrant SALA");
				if(Sales.quantitatSales()==0) //NO hi ha sales
					System.out.println("ERROR Esborra SALA: No hi ha sales a esborrar");
				else{ //Hi ha sales creades
					Sales.llistarSales();
					sala = Validacio.validaSencer("\t Tria SALA a esborrar:",Sales.quantitatSales());
					Sales.esborraSala(sala);
				}
				break;

			case 4: //Crear SESSIO
				System.out.println("Creant SESSIO");
				Sessio se = new Sessio();
				System.out.println(se);
				sessions.afegirSessio(se);
				break;

			case 5: //Modifica SESSIO
				System.out.println("Modificant SESSIO");

				if(Sessions.quantitatSessions()==0) //NO hi ha sessions
					System.out.println("ERROR Modifica SESSIO: No hi ha sessions a modificar");
				else{ //Hi ha sessions creades
					Sessions.llistarSessions();
					sessio = Validacio.validaSencer("\t Tria SESSIO a modificar:",Sessions.quantitatSessions());
					Sessions.modificaSessio(sessio);
				}
				break;

			case 6: //Esborrar SESSSIO
				System.out.println("Esborrant SESSIO");
				if(Sessions.quantitatSessions()==0) //NO hi ha sessions
					System.out.println("ERROR Esborra SESSIO: No hi ha sessions a modificar");
				else{ //Hi ha sessions creades
					Sessions.llistarSessions();
					sessio = Validacio.validaSencer("\t Tria SESSIO a esborrar:",Sessions.quantitatSessions());
					Sessions.esborraSessio(sessio);
				}
				break;


			case 7: //Crear PELICULA
				System.out.println("Creant PELICULA");
				Pelicula p = new Pelicula();
				System.out.println(p);
				pelicules.afegirPelicula(p);
				break;

			case 8: //Modifica PELICULA
				System.out.println("Modificant PELICULA");

				if( Pelicules.quantitatPelicules()==0) //NO hi ha pelicules
					System.out.println("ERROR Modifica PELICULA: No hi ha pelicules a modificar");
				else{ //Hi ha pelicules creades
					Pelicules.llistarPelicules();
					pelicula = Validacio.validaSencer("\t Tria PELICULA a modificar:", Pelicules.quantitatPelicules());
					Pelicules.modificaPelicula(pelicula);
				}
				break;

			case 9: //Esborrar PELICULA
				System.out.println("Esborrant PELICULA");
				if( Pelicules.quantitatPelicules()==0) //NO hi ha pelicules
					System.out.println("ERROR Esborra PELICULA: No hi ha pelicules a esborrar");
				else{ //Hi ha pelicules creades
					Pelicules.llistarPelicules();
					pelicula = Validacio.validaSencer("\t Tria PELICULA a esborrar:", Pelicules.quantitatPelicules());
					Pelicules.esborraPelicula(pelicula);
				}
				break;

			case 10: //Comprar ENTRADA
				System.out.println("Comprant ENTRADA");
				FilCompraEntradesClient objNou1, objNou2;
				Thread filNou1 = null, filNou2 = null; 

				//COMPRA ENTRADES DE L'APLICACIO		
				//compraEntradaPelicula();

				//COMPRA ENTRADES FIL INTERACTIVA
				//===============================
				//FilCompraEntradesInteractiva nou1 = new FilCompraEntradesInteractiva();
				//FilCompraEntrades nou2 = new FilCompraEntrades();
				//Thread noufil1 = new Thread(nou1);
				//Thread noufil2 = new Thread();				
				//noufil1.start();
				//noufil1.join();
				 
				
				
				ServerSocket serversocket = new ServerSocket(puertoEscucha);
				System.out.println("Executing the SERVER thread to attend TCP requests…");
				socketClient = serversocket.accept();
				

				servidor = new FilServidorCompraEntrades(socketClient, contServidor++);
				servidor.start();
				
				//COMPRA ENTRADES FIL NO INTERACTIVA
				//==================================
				//simulem la concurrencia de compra de 2 clients amb un bucle

				/*
				for (int j=1; j<=2; j++) {
					System.out.println("\n----------------------\n\nInici de l'introduccio de dades de l'usuari "+ j+ "\n----------------------\n");
					//Si NO hi ha PELICULES, s'ix del procés de compra
					if (Pelicules.llistarPelicules() == 0) {
						System.out.println("\t ERROR Cine:compraEntradaPelicula: No hi ha PELICULES");
						return;
					}
					//Selecció de PELICULA
					pelicula = Validacio.validaSencer("\t [Usuari "+j+"] Tria PELICULA:",Pelicules.quantitatPelicules());
					p = Pelicules.retornaPelicula(pelicula);
					System.out.println(p);
					System.out.println();
					System.out.println();

					//Si NO hi ha SESSIONS per la PELICULA, s'ix del procés de compra
					if (p.llistarSessionsPeli()== 0) {
						System.out.println("\t ERROR Cine:compraEntradaPelicula: No hi ha SESSIONS per a esta PELICULA");
						return;
					}
					//Selecció de la SESSIO
					sessio = Validacio.validaSencer("\t [Usuari "+j+"] Tria la sessió per a "+p.getNomPeli()+":",p.getSessionsPeli().size());

					se = p.retornaSessioPeli(sessio);
					sa = se.getSala();
					se.mapaSessio();

					//Selecció de QUANTES entrades es volen comprar
					numEntrades = Validacio.validaSencer("\t [Usuari "+j+"] Quantes ENTRADES vols comprar? ",sa.getFiles()*sa.getTamanyFila());
					Seient[][] seients = se.getSeients();

					//Selecció dels seients en funció del num entrades a comprar
					SeientsReservats seientsAcomprar = new SeientsReservats();

					for (i=0; i < numEntrades; i++){
						System.out.println("\tSeient "+(i+1)+" :");
						int fila = Validacio.validaSencer("\t\t [Usuari "+j+"]  Tria FILA: [1-"+sa.getFiles()+"] ",sa.getFiles());
						int seient = Validacio.validaSencer("\t\t [Usuari "+j+"]  Tria SEIENT en la fila: [1-"+sa.getTamanyFila()+"]",sa.getTamanyFila());		
						//Seient[][] seients = se.getSeients();
						
						//seients[fila-1][seient-1].reservantSeient();	
						seientsAcomprar.afegirSeient(seients[fila-1][seient-1]);//afegeix seient a llista SEIENTS RESERVATS
						
						if ((i+1)!=numEntrades)
						System.out.println("\t [Usuari "+j+"] Següent Seient...");
					}//endfor

					if (j==1) {
						objNou1 = new FilCompraEntradesInteractiva("fil1", p, se, numEntrades, seientsAcomprar);
						filNou1 = new Thread(objNou1);
					}else { 
						objNou2 = new FilCompraEntradesInteractiva("fil2", p, se, numEntrades, seientsAcomprar);	
						filNou2 = new Thread(objNou2);
					}
				}//endfor
				
				filNou1.start();
				filNou2.start();

				filNou1.join();
				filNou2.join();
				*/
				break;

			default: System.out.println("Eixint CINE");
			}
		}while(opcio!=0);

	}

	//-------------------------

	public static Boolean pagamentEntrada(String nom, int reserva, BigDecimal preu) throws InterruptedException{
		System.out.println(nom + "Import a pagar: "+preu.multiply(new BigDecimal(reserva)));
		System.out.println(nom + "\nPagant...");
		//pagant
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Validacio.validaBoolea(nom +"Pagat? (S/N) ");
	}
	//---------------------
	public static int menu(){
		int opcio;
		Scanner s = new Scanner(System.in);

		do{
			System.out.println("MENU CINE:");
			System.out.println("====================");
			System.out.println("1.  Crear SALA");
			System.out.println("2.  Modificar SALA");
			System.out.println("3.  Eliminar SALA");

			System.out.println("4.  Crear SESSIO");
			System.out.println("5.  Modificar SESSIO");
			System.out.println("6.  Eliminar SESSIO");

			System.out.println("7.  Crear PELICULA");
			System.out.println("8.  Modificar PELICULA");
			System.out.println("9.  Eliminar PELICULA");

			System.out.println("10. Comprar ENTRADA");
			System.out.println("0. Eixir CINE");

			String stropcio = s.next();
			opcio=Integer.parseInt(stropcio);
		}while (opcio < 0 || opcio > 10);

		return opcio;
	}

	public static void carregaDades(Sales sales, Sessions sessions, Pelicules pelicules){
		sales.afegirSala(new Sala(1, 5, 5));
		sales.afegirSala(new Sala(2, 7, 7));
		sessions.afegirSessio(new Sessio("sessio1", Calendar.getInstance(), sales.retornaSala(1), new BigDecimal(4)));
		sessions.afegirSessio(new Sessio("sessio2", Calendar.getInstance(), sales.retornaSala(1), new BigDecimal(4)));
		pelicules.afegirPelicula(new Pelicula("Avatar","AMERICANA", 250,"JAMES CAMERON","INTERPRET1, INTERPRET2", "ARGUMENT", "FICCIO","TOTS PUBLICS",sessions.getSessions()));

	}

}