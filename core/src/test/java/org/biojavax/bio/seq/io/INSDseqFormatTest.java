package org.biojavax.bio.seq.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import junit.framework.TestCase;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojavax.Namespace;
import org.biojavax.Note;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.io.INSDseqFormat.Terms;
import org.biojavax.bio.taxa.NCBITaxon;

/**
 * Tests for INSD Format.
 * @author George Waldon
 */
public class INSDseqFormatTest extends TestCase {
    private INSDseqFormat insdFormat;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() {
        this.insdFormat = new INSDseqFormat();
    }

    public void testINSDParsing() {
	RichSequence sequence = readFile("/NM_008366.xml");
        assertEquals("NM_008366", sequence.getName());
        assertFalse(sequence.getCircular());
        assertEquals("Mus musculus interleukin 2 (Il2), mRNA", sequence.getDescription());
        assertEquals("ROD", sequence.getDivision());
        NCBITaxon taxon = sequence.getTaxon();
        assertNotNull(taxon);
        assertEquals(2, sequence.getVersion());
        String stranded = null;
        String udat = null;
        String molType = sequence.getAlphabet().getName();
        for (Iterator i = sequence.getNoteSet().iterator(); i.hasNext(); ) {
            Note n = (Note)i.next();
            if (n.getTerm().equals(Terms.getStrandedTerm())) stranded=n.getValue();
            else if (n.getTerm().equals(Terms.getDateUpdatedTerm())) udat=n.getValue();
            else if (n.getTerm().equals(Terms.getMolTypeTerm())) molType=n.getValue();
        }
        assertNotNull(stranded);
        assertEquals("single", stranded);
        assertNotNull(udat);
        assertEquals("25-MAR-2007", udat);
        assertNotNull(molType);
        assertEquals("mRNA", molType);
    }


    /** Test whether the parser reads minimal sequences. The sequence prototype
     * was generated by writing a sequence read in fasta format 
     * (">testempty no sequence") under the tested format.
     */
    public void testReadEmptySequence() {
        RichSequence sequence = readFile("/empty_insdseq.xml");
        assertNotNull(sequence);
        assertEquals(sequence.getName(), "testempty");
        assertEquals(sequence.getAccession(), "");
        assertEquals(sequence.getVersion(), 0);
        assertEquals(sequence.getDescription(), "no sequence");
        assertEquals(sequence.getInternalSymbolList().length(), 0);
    }
   
    /**
     * Read a sequence file, return a RichSequence
     * @param filename name of file to read
     * @return a RichSequence instance
     */
    private RichSequence readFile(String filename) {
	InputStream inStream = this.getClass().getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        SymbolTokenization tokenization = RichSequence.IOTools.getDNAParser();
        Namespace namespace = RichObjectFactory.getDefaultNamespace();
        SimpleRichSequenceBuilder builder = new SimpleRichSequenceBuilder();
        RichSequence sequence = null;
        try {
            this.insdFormat.readRichSequence(br, tokenization, builder, namespace);
            sequence = builder.makeRichSequence();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unexpected exception: "+e);
        }
		return sequence;
	}
}