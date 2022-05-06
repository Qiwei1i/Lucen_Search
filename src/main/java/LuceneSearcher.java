import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Use Lucene to build indexes and search
 * */
public class LuceneSearcher implements Searcher{
    // All the paragraphs in the book
    private List<Paragraph> paragraphs;
    private Analyzer analyzer=new StandardAnalyzer();
    private final Path indexPath= Paths.get("indexdir");

    /**
     * Build Lucene index
     * @param p All the paragraphs in the book
     * */
    public LuceneSearcher(List<Paragraph> p){
        paragraphs=p;
        IndexWriterConfig icw=new IndexWriterConfig(analyzer);
        icw.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir=null;
        IndexWriter inWriter=null;

        try{
            if(!Files.isReadable(indexPath)){
                Files.createDirectory(indexPath);
            }
            dir= FSDirectory.open(indexPath);
            inWriter=new IndexWriter(dir,icw);
            for(Paragraph paragraph:paragraphs){
                Document document=new Document();
                document.add(new TextField("content",paragraph.getContent(), Field.Store.YES));
                document.add(new StoredField("index",paragraph.getIndex()));
                inWriter.addDocument(document);
            }
            inWriter.close();
            dir.close();
        }catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * Use lucene index to search keyword in paragraphs
     * @param term search keyword
     * @return the content of paragraphs which contain the keyword
     * */
    @Override
    public List<String> search(String term) throws Exception {
        List<String> results=new ArrayList<>();
        Directory dir=FSDirectory.open(indexPath);
        IndexReader reader= DirectoryReader.open(dir);
        IndexSearcher searcher=new IndexSearcher(reader);
        Query q=new QueryParser("content",analyzer).parse(term);
        TopDocs hits=searcher.search(q,reader.maxDoc());
        for(ScoreDoc sd:hits.scoreDocs){
            Document doc=searcher.doc(sd.doc);
            results.add(doc.get("content"));
        }

        return results;
    }


}
