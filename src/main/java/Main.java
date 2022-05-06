import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    private static List<Paragraph> readInputFile(String filename) throws IOException {
        BufferedReader reader=new BufferedReader(new FileReader(filename));
        String line;
        List<Paragraph> paragraphs=new ArrayList<>();
        int index=0;
        StringBuilder content= new StringBuilder();
        while ((line=reader.readLine())!=null){
            if(line.isEmpty()){
                Paragraph tmp=new Paragraph(content.toString(),index++);
                paragraphs.add(tmp);
                content=new StringBuilder();
            }else{
                content.append(line).append("\n");
            }

        }
        if(!content.isEmpty()){
            Paragraph tmp=new Paragraph(content.toString(),index++);
            paragraphs.add(tmp);
        }
        reader.close();
        return paragraphs;
    }
    /**
     * main - Instantiates user GUI and loads documents from library.
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        // Instantiate an instance of the UserGUI
        List<Paragraph> paragraphs=readInputFile("pg67953.txt");
        Searcher indexSearcher=new LuceneSearcher(paragraphs);
        Searcher bfSearcher=new BFSearch(paragraphs);
        new MainView(indexSearcher,bfSearcher);
    }
}
