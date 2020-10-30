package persistence;

import model.ScheduleMaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class JsonWriter {
    public static Writer writer;

    public static void saveFile(ScheduleMaker schedule, String fileName) throws IOException {
        writer = new FileWriter(new File("./data/timetables", fileName + ".json"));
        FileFormat file = new FileFormat(schedule);
        writer.write(file.createObject().toString());
        writer.close();


    }
}
