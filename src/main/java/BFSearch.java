import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BFSearch implements Searcher{
    private List<Paragraph> paragraphs;

    public BFSearch(List<Paragraph> p) {
        paragraphs = p;
    }

    private boolean containsWord(String content, String word){

        String searchWord=word.toLowerCase(Locale.ROOT);

        Pattern p = Pattern.compile("(\\s|\\b)%s(\\s|\\b)".formatted(searchWord));
        Matcher m = p.matcher(content.toLowerCase(Locale.ROOT));
        return m.find();

    }

    @Override
    public List<String> search(String term) throws Exception {
        List<String> result=new ArrayList<>();
        term=term.toLowerCase(Locale.ROOT);
        for(Paragraph p:paragraphs){
            if(containsWord(p.getContent(),term)){
                result.add(p.getContent());
            }
        }
        return result;
    }
}
