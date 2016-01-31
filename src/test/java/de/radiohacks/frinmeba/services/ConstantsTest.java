/**
 * 
 */
package de.radiohacks.frinmeba.services;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author hcmai
 *
 */
public class ConstantsTest {

    @Test
    public void testGetUploadFolder() {
        Constants o = new Constants();
        assertNotNull(o);
        
        File file = new File(o.getUploadFolder());
        assertThat(file.canWrite(), is(true));
    }
    
    @Test
    public void testGetUploadFolderVideo() {
        Constants o = new Constants();
        assertNotNull(o);
        
        File file = new File(o.getUploadFolderVideo());
        assertThat(file.canWrite(), is(true));
    }
    
    @Test
    public void testGetUploadFolderImage() {
        Constants o = new Constants();
        assertNotNull(o);
        
        File file = new File(o.getUploadFolderImage());
        assertThat(file.canWrite(), is(true));
    }
    
    @Test
    public void testGetUploadFolderFile() {
        Constants o = new Constants();
        assertNotNull(o);
        
        File file = new File(o.getUploadFolderFiles());
        assertThat(file.canWrite(), is(true));
    }

}
