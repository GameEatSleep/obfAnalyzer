import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FolderTraverse {
	Map<String,File> foundFiles = new HashMap<String,File>();
	public void walk( String path ) {
        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath() );
            } else {
            	String fileNameOnly = f.getName().replaceFirst("[.][^.]+$", "");
            	foundFiles.put(fileNameOnly,f);
            }
        }
	}
}