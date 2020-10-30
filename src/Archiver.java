import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class Archiver {
    public static void main(String[] args) throws WrongPathException, IOException {

        JFileChooser jFileChooser = new JFileChooser();

        int r = jFileChooser.showOpenDialog(null); // выбор первого файла

        if (r != JFileChooser.APPROVE_OPTION)
        {
            throw new WrongPathException("Didn't choose file");
        }

        ArrayList<String> files1 = new ArrayList<String>(); // имена файлов1
        ZipFile zipFile1 = new ZipFile(jFileChooser.getSelectedFile().getAbsolutePath());
        Enumeration zipEntries1 = zipFile1.entries();
        while (zipEntries1.hasMoreElements()) {
            files1.add(((ZipEntry) zipEntries1.nextElement()).getName());
        }

        r = jFileChooser.showOpenDialog(null); // выбор второго файла

        if (r != JFileChooser.APPROVE_OPTION)
        {
            throw new WrongPathException("Didn't choose the file");
        }

        ArrayList<String> files2 = new ArrayList<String>(); // имена файлов2
        ZipFile zipFile2 = new ZipFile(jFileChooser.getSelectedFile().getAbsolutePath());
        Enumeration zipEntries2 = zipFile2.entries(); //перечисление файлов
        while (zipEntries2.hasMoreElements()) {
            files2.add(((ZipEntry) zipEntries2.nextElement()).getName()); // добавление имён файлов
        }
        int[] flags1 = new int[files1.size()];
        int[] flags2 = new int[files2.size()];
        for (int i = 0; i<files1.size(); i++){ // отсчёт первого архива
            boolean f = true;
            for (int j = 0; j< files2.size(); j++) // отсчёт второго архива
            {
                if (files1.get(i).equals(files2.get(j)) && zipFile1.getEntry(files1.get(i)).getSize() == zipFile2.getEntry(files2.get(j)).getSize()) {
                    f = false; // файл удалён если нет ни одного файла из второго архива с таким же именем и размером
                    break;
                }
            }
            if (f) flags1[i] = 1; // запись удалена
        }

        for (int i = 0; i<files1.size(); i++){ // отсчёт первого архива
            for (int j = 0; j< files2.size(); j++) // отсчёт второго архива
            {
                if (zipFile1.getEntry(files1.get(i)).getSize() == zipFile2.getEntry(files2.get(j)).getSize() && !files1.get(i).equals(files2.get(j))){
                    flags1[i] = 2; // переименована если есть файл с таким же размером и другим именем
                    flags2[j] = 2; //
                }
                if (zipFile1.getEntry(files1.get(i)).getSize() != zipFile2.getEntry(files2.get(j)).getSize() && files1.get(i).equals(files2.get(j))){
                    flags1[i] = 4; // изменен если есть файл с таким же именем но другим размером
                    flags2[j] = 4; //
                }
            }
        }

        for (int i = 0; i<files2.size(); i++) { // отсчёт второго архива
            boolean f = true;
            for (int j = 0; j < files1.size(); j++) // отсчёт первого архива
            {
                if (zipFile1.getEntry(files1.get(j)).getSize() == zipFile2.getEntry(files2.get(i)).getSize() && files1.get(j).equals(files2.get(i))) {
                    f = false; // добавлен если не разу не встретился файл с таким же именем и размером
                }
            }
            if (f && flags2[i] != 0) f = false; // если уже стоит флаг, то ничего не делаем
            if (f) flags2[i] = 3; // запись добавлена
        }
        int length = Math.max(flags1.length, flags2.length);
        int flags1Length = flags1.length;
        int flags2Length = flags2.length;
        flags1 = Arrays.copyOf(flags1, length);
        flags2 = Arrays.copyOf(flags2, length); // выравнивание размеров массивов


        String[] res1 = new String[length+2];
        String[] res2 = new String[length+2];
        res1[0] = zipFile1.getName().substring(zipFile1.getName().lastIndexOf('\\')+1) + "\t\t|";
        res2[0] = zipFile1.getName().substring(zipFile1.getName().lastIndexOf('\\')+1) + "\n\r";
        res1[1] = "-------------\t| ";
        res2[1] = "-------------\n\r";

        for (int i = 0; i < length; i++) // заполнение массивов для вывода
        {
            if (flags1[i] == 1) {
                res1[i+2] = "- " + files1.get(i) + "\t\t|";
            }
            else if (flags1[i] == 2) {
                res1[i+2] = "? " + files1.get(i) + "\t\t|";
            }
            else if (flags1[i] == 3) {
                res1[i+2] = "+ " + files1.get(i) + "\t\t|";
            }
            else if (flags1[i] == 4) {
                res1[i+2] = "* " + files1.get(i) + "\t\t|";
            }
            else if(i < flags1Length) {
                res1[i+2] = "  " + files1.get(i) + "\t\t|";
            }
            else res1[i+2] = "\t\t\t\t|";

            if (flags2[i] == 1) {
                res2[i+2] = "- " + files2.get(i) + "\n\r";
            }
            else if (flags2[i] == 2) {
                res2[i+2] = "? " + files2.get(i) + "\n\r";
            }
            else if (flags2[i] == 3) {
                res2[i+2] = "+ " + files2.get(i) + "\n\r";
            }
            else if (flags2[i] == 4){
                res2[i+2] = "* " + files2.get(i) + "\n\r";
            }
            else if (i < flags2Length) {
                res2[i+2] = "  " + files2.get(i) + "\n\r";
            }
            else res2[i+2] = "\n\r";
        }

        for (int i = 0; i < length+2; i++)
        {
            System.out.print(res1[i] + " " + res2[i]);
        }
    }
}
