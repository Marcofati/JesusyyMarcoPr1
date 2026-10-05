
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Configurador {
    ArrayList<String> archivos;
    ArrayList<String> algoritmos;
    ArrayList<Long> semillas;
    Integer parametroextra;

    public Configurador(String ruta) {
        archivos=new ArrayList<>();
        algoritmos=new ArrayList<>();
        semillas=new ArrayList<>();
        String linea;
        FileReader f=null;
        try{
            f = new FileReader(ruta);
            BufferedReader b = new BufferedReader(f);
            while((linea=b.readLine())!=null) {
                String[] split = linea.split("=");
                switch(split[0]) {
                    case "Archivos":
                        //archivos.add(split[1]);
                        String[] v= split[1].split(" ");
                        for (int i = 0; i < v.length; i++){
                            archivos.add(v[i]);
                        }
                        break;
                    case "Semillas":
                        //semillas.add(split[1]);
                        String[] vsem= split[1].split(" ");
                        for (int i = 0; i < vsem.length; i++){
                            semillas.add(Long.parseLong(vsem[i]));
                        }
                        break;
                    case "Algoritmos":
                        //algoritmos.add(split[1]);
                        String[] valgorit= split[1].split(" ");
                        for (int i = 0; i < valgorit.length; i++){
                            algoritmos.add(valgorit[i]);
                        }
                        break;
                    case "OtrosParametros":
                        parametroextra= Integer.parseInt(split[1]);
                        break;            
                }
            }

        }catch(IOException e) {
            System.out.println(e);
        }
    }

    public ArrayList<String> getArchivos() {
        return archivos;
    }

    public ArrayList<String> getAlgoritmos() {
        return algoritmos;
    }

    public ArrayList<Long> getSemillas() {
        return semillas;
    }

    public Integer getParametroextra() {
        return parametroextra;
    }

}
