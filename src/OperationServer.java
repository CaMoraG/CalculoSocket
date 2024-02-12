import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class OperationServer {
    public static void main(String[] args) {
        try {
            int opserverId = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(12345+opserverId);
            System.out.println("Servidor de operación "+ opserverId +" esperando conexiones...");

            while (true) {
                //Espera la conexión con el servidor de cálculo
                Socket calculationServer = serverSocket.accept();
                System.out.println("Servidor de cálculo conectado desde " + calculationServer.getInetAddress());

                //Recibe al subarreglo
                ObjectInputStream ois = new ObjectInputStream(calculationServer.getInputStream());
                int[] subarray = (int[]) ois.readObject();

                //Ordena el subarreglo
                subarray = sortArray(subarray);
                System.out.println("Arreglo ordenado");

                //Devuelve el subarreglo ordenado
                calculationServer = new Socket("localhost", 12345);
                ObjectOutputStream oos = new ObjectOutputStream(calculationServer.getOutputStream());
                oos.writeObject(subarray);
                oos.flush();

                oos.close();
                ois.close();
                calculationServer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static int[] sortArray(int[] subarray) {
        int n = subarray.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (subarray[j] > subarray[j+1]) {
                    int temp = subarray[j];
                    subarray[j] = subarray[j+1];
                    subarray[j+1] = temp;
                }
            }
        }
        return subarray;
    }
}
