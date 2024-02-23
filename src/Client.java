import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        Socket calculationServer = new Socket("10.43.100.120", 12345);
        System.out.println("Servidor de cálculo conectado desde " + calculationServer.getInetAddress());

        System.out.print("Introduzca el tamaño del arreglo a ordenar: ");
        int size = scanner.nextInt();
        int[] array = new int[size];
        System.out.print("Introduzca los numeros del arreglo de tamaño "+size+"\n");
        for(int i=0;i<size;i++){
            array[i]=scanner.nextInt();
        }

        System.out.print("Arreglo sin ordenar: "+Arrays.toString(array)+"\n");

        //Envia el arreglo
        ObjectOutputStream oos = new ObjectOutputStream(calculationServer.getOutputStream());
        oos.writeObject(array);
        oos.flush();

        //Recibe el arreglo ordenado
        ObjectInputStream ois = new ObjectInputStream(calculationServer.getInputStream());
        array = (int[]) ois.readObject();

        System.out.print("Arreglo ordenado: ");
        System.out.print(Arrays.toString(array));

        calculationServer.close();
    }
}
