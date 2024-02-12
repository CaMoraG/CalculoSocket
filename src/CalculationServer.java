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
                Socket client = server.accept();
                System.out.println("Cliente conectado desde " + client.getInetAddress());

                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                int[] array = (int[]) ois.readObject();

                int size = array.length;
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

                Socket operationServer1Socket = new Socket("localhost", 12346);
                ObjectOutputStream oos = new ObjectOutputStream(operationServer1Socket.getOutputStream());
                oos.writeObject(top);
                oos.flush();
                operationServer1Socket.close();

                Socket operationServer2Socket = new Socket("localhost", 12347);
                oos = new ObjectOutputStream(operationServer2Socket.getOutputStream());
                oos.writeObject(bottom);
                oos.flush();
                operationServer2Socket.close();

                operationServer1Socket = server.accept();
                ois = new ObjectInputStream(operationServer1Socket.getInputStream());
                top = (int[]) ois.readObject();
                operationServer1Socket.close();

                operationServer2Socket = server.accept();
                ois = new ObjectInputStream(operationServer2Socket.getInputStream());
                bottom = (int[]) ois.readObject();
                operationServer2Socket.close();

                System.out.println("Subarreglo1: "+ Arrays.toString(top));
                System.out.println("Subarreglo2: "+ Arrays.toString(bottom));

                array = jointArrays(top, bottom, size);
                System.out.println("Arreglo ordenado");
                oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(array);
                oos.flush();
                oos.close();

                ois.close();
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
