import com.google.gson.Gson;

public class FileInfo {
    private String FileName;
    private int FileSize;

    public FileInfo(String fileName, int fileSize){
        this.FileName = fileName;
        this.FileSize = fileSize;
    }

    public FileInfo(String fi){
        Gson gson = new Gson();
        FileInfo fileInfo = gson.fromJson(fi, FileInfo.class);
        this.FileName = fileInfo.FileName;
        this.FileSize = fileInfo.FileSize;
    }

    public void setFileName(String fn) {
        this.FileName = fn;
    }
    public String getFileName() {
        return this.FileName;
    }
    public void setFileSize(int sz) {
        this.FileSize = sz;
    }
    public int getFileSize() {
        return this.FileSize;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
