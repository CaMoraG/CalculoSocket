import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class CalculationServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(12345);
            System.out.println("Servidor de cálculo esperando conexiones...");

            while (true) {
                //Recibe la conexión del cliente
                Socket client = server.accept();
                System.out.println("Cliente conectado desde " + client.getInetAddress());

                //Recibe el arreglo del cliente
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                int[] array = (int[]) ois.readObject();
                System.out.println("Arreglo recibido " + Arrays.toString(array));

                //Divide el arreglo a la mitad
                int[][] r = divideArray(array, array.length);
                int[] top = r[0];
                int[] bottom = r[1];
                Socket operationServer1Socket;
                Socket operationServer2Socket;
                //Verifica conexiones
                //Envia la primera mitad al servidor de operacion 1
                try {
                    operationServer1Socket = new Socket("10.41.103.37", 12346);
                    sendSubarray(top, operationServer1Socket);
                    top = receiveSubarray(server);
                } catch (IOException e) {
                    System.err.println("Error al conectar al servidor de operacion 1: " + e.getMessage());
                    operationServer2Socket = new Socket("10.43.100.149", 12347);
                    sendSubarray(top, operationServer2Socket);
                    top = receiveSubarray(server);
                }
                //Envia la segunfa mitad al servidor de operacion 2
                try {
                    operationServer2Socket = new Socket("10.43.100.149", 12347);
                    sendSubarray(bottom, operationServer2Socket);
                    bottom = receiveSubarray(server);
                } catch (IOException e) {
                    System.err.println("Error al conectar al servidor de operacion 2: " + e.getMessage());
                    operationServer1Socket = new Socket("10.41.103.37", 12346);
                    sendSubarray(bottom, operationServer1Socket);
                    bottom = receiveSubarray(server);
                }

                //Une los subarreglos y da el arreglo original pero ordenado que es enviado de vuelta al cliente
                array = jointArrays(top, bottom, array.length);
                System.out.println("Arreglo ordenado");
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(array);
                oos.flush();
                oos.close();

                ois.close();
                client.close();
                System.out.println("Arreglo devuelto " + Arrays.toString(array));
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static int[][] divideArray(int[] array, int size){
        int topsize, bottomsize, bottomindex;
        if(size%2==0){
            bottomindex = topsize = bottomsize = size/2;
        } else{
            topsize = size/2;
            bottomindex = bottomsize = (size/2)+1;
            bottomindex--;
        }
        int[] top = new int[topsize];
        int[] bottom = new int[bottomsize];
        System.arraycopy(array,0,top,0,topsize);
        System.arraycopy(array,bottomindex,bottom,0,bottomsize);
        return new int[][]{top, bottom};
    }
    public static void sendSubarray(int[] subarray, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(subarray);
        oos.flush();
        oos.close();
    }

    public static int[] receiveSubarray(ServerSocket server) throws IOException, ClassNotFoundException {
        Socket socket = server.accept();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        int[] r = (int[]) ois.readObject();
        ois.close();
        socket.close();

        return r;
    }
    private static int[] jointArrays(int[] arr1, int[] arr2, int size){
        int i = 0, j = 0, k = 0;
        int[] finalArray = new int[size];
        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] < arr2[j]) {
                finalArray[k++] = arr1[i++];
            } else {
                finalArray[k++] = arr2[j++];
            }
        }
        while (i < arr1.length) {
            finalArray[k++] = arr1[i++];
        }
        while (j < arr2.length) {
            finalArray[k++] = arr2[j++];
        }
        return finalArray;
    }
}
