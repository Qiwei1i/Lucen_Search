import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView {

    private final int COLUMNS = 2;
    private final int ROWS = 3;
    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 500;

    private JButton searchButton;
    private JButton bfSearchButton;

    private JFrame frame;
    private JPanel gridbagPanel;
    private JPanel displayArea;
    private JTextField searchField;
    private JScrollPane scrollPane;
    private Searcher searcher;
    private Searcher indexSearcher;
    private Searcher bfSearcher;

    /**
     * Constructor - Creates layout of user GUI.
     */
    public MainView(Searcher indexSearcher,Searcher bfSearcher)
    {
        this.indexSearcher=indexSearcher;
        this.bfSearcher=bfSearcher;
        gridbagPanel = new JPanel(new GridBagLayout());
        gridbagPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gridbagPanel.setLayout(new GridBagLayout());

        frame = new JFrame("Text Search");
        frame.setContentPane(gridbagPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildGridBagPanel();
        // Set search result display area attributes
        //displayArea.set
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Build window and set to visible
        frame.pack();
        frame.setVisible(true);

        // Set window size and lock
        frame.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
    }

    /**
     * buildGridBagPanel - Builds a GridBag layout panel with a text area, text
     * field, and buttons.
     */
    public void buildGridBagPanel()
    {
        // Set default font
        Font font = new Font(null, Font.BOLD, 14);

        GridBagConstraints button = new GridBagConstraints();
        GridBagConstraints textArea = new GridBagConstraints();
        GridBagConstraints textField = new GridBagConstraints();

        searchButton = new JButton("Index");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clear();
                    setSearcher(indexSearcher);
                    searchResult();
                }catch (IOException a){
                    a.printStackTrace();
                }

            }
        });
        searchButton.setFont(font);

        bfSearchButton = new JButton("BF");
        bfSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clear();
                    setSearcher(bfSearcher);
                    searchResult();
                }catch (IOException a){
                    a.printStackTrace();
                }

            }
        });
        bfSearchButton.setFont(font);



        // Instantiate text area, scroll pane, and text field
        displayArea = new JPanel();
        displayArea.setLayout(new BoxLayout(displayArea,1));
        //displayArea.setEditable(false);

        scrollPane = new JScrollPane(displayArea);
        scrollPane.setBackground(Color.WHITE);
        searchField = new JTextField();

        // Set the font of the results area
        displayArea.setFont(new Font(null, Font.BOLD, 18));

        // GridBag Constraints
        button.anchor = GridBagConstraints.LINE_END;
        button.fill = GridBagConstraints.NONE;
        button.insets = new Insets(5, 5, 5, 86);
        button.weightx = 0;

        textArea.anchor = GridBagConstraints.LINE_START;
        textArea.fill = GridBagConstraints.BOTH;
        textArea.gridwidth = 2;
        textArea.insets = new Insets(5, 5, 5, 5);
        textArea.weightx = 1;
        textArea.weighty = 1;

        textField.anchor = GridBagConstraints.LINE_START;
        textField.fill = GridBagConstraints.HORIZONTAL;
        textField.gridwidth = 2;
        textField.insets = new Insets(5, 5, 5, 178);
        textField.ipady = 9;
        textField.weightx = 1;

        // Populate GridBag layout
        for (int i = 0; i < ROWS; i++)
        {
            button.gridy = i;
            textArea.gridy = i;
            textField.gridy = i;

            for (int j = 0; j < COLUMNS; j++)
            {
                button.gridx = j;
                textArea.gridx = j;
                textField.gridx = j;

                switch (i)
                {
                    case 0:
                        // If first row, first column
                        if (j == 0)
                        {
                            gridbagPanel.add(searchField, textField);
                        }
                        // If first row, second column
                        if (j == 1) {
                            gridbagPanel.add(searchButton, button);

                            button.insets = new Insets(5, 5, 5, 5);

                            gridbagPanel.add(bfSearchButton, button);

                        }

                        break;
                    case 1:
                        // If second row, first column
                        if (j == 0)
                        {
                            gridbagPanel.add(scrollPane, textArea);
                        }
                        break;
                    case 2:
                        // If third row, first column
                        if (j == 0)
                        {
                            button.anchor = GridBagConstraints.LINE_START;

                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    private void highlightWords(Highlighter highlighter, String content, String word){

        Highlighter.HighlightPainter painter =
                new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
        String searchWord=word.toLowerCase(Locale.ROOT);

        Pattern p = Pattern.compile("(\\s|\\b)%s(\\s|\\b)".formatted(searchWord));
        Matcher m = p.matcher(content.toLowerCase(Locale.ROOT));

        while(m.find()) {
            try {
                highlighter.addHighlight(m.start()+1, m.end(), painter );
            }catch (BadLocationException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * createResults - populates the search results panel
     * @param text String the full paragraph text
     */
    private void createResults(String text) throws IOException {
        JTextArea bResults = new JTextArea();
        bResults.setEditable(false);

        bResults.setBounds(100, 100, 150, 200);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        bResults.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        bResults.setText(text);

        bResults.setBackground(Color.WHITE);

        bResults.setFont(new Font(null, Font.BOLD, 12));

        Highlighter highlighter = bResults.getHighlighter();
        String searchWords=searchField.getText();
        Analyzer analyzer=new StandardAnalyzer();
        StringReader reader=new StringReader(searchWords);
        TokenStream toStream=analyzer.tokenStream(searchWords,reader);
        toStream.reset();
        CharTermAttribute termAttribute=toStream.getAttribute(CharTermAttribute.class);
        while (toStream.incrementToken()){
            highlightWords(highlighter,text,termAttribute.toString());
        }
        displayArea.add(bResults);
        scrollPane.setViewportView(displayArea);
    }

    /**
     * clear - Clears search field and search results display area.
     */
    private void clear()
    {
        displayArea.removeAll();
        scrollPane.setViewportView(displayArea);
    }

    private void searchResult() throws IOException {
        displayArea.removeAll();
        scrollPane.setViewportView(displayArea);
        // Check input and display error if necessary
        if (searchField.getText().equals(""))
        {
            JOptionPane.showMessageDialog(null, "No results for " + searchField.getText() + ".");
        }
        else
        {
            // Create a helper object and pass the text from the searchField

            // Return the list of results
            List<String> results = null;
            try {
                results = searcher.search(searchField.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Check if the list return is null or empty
            if (results == null || results.isEmpty())
            {
                JTextArea noResults = new JTextArea();
                noResults.setText("No results found for " + searchField.getText());
                noResults.setEditable(false);
                displayArea.add(noResults);
                scrollPane.setViewportView(noResults);
            }
            else{
                // For each paragraph's toString: append it to the string builder
                for (String para : results) {
                    createResults(para);
                }

            }
        }
    }
}
