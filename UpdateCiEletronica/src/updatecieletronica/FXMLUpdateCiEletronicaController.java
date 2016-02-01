/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package updatecieletronica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.System.exit;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author victorcmaf
 */
public class FXMLUpdateCiEletronicaController implements Initializable {
    
    @FXML
    private Label lblPaso01;
    @FXML
    private Label lblPaso02;
    @FXML
    private Label lblPaso03;
    @FXML
    private Label lblPaso04;
    @FXML
    private Button btnStartUpdate;
    @FXML
    private Button btnFechar;
    
    private boolean bUpdateFeito = true;
    
    @FXML
    private void hdlButtonFechar(ActionEvent event) {
        //System.out.println("Fechar aplicação");
        exit(0);
       
    }
    @FXML
    private void hdlButtonStartUpdate(ActionEvent event){        
        boolean bDeletadoCiEletronica = false;
        boolean bDeletadoSql = false;
        File fJar = null;        
        
        
        //Primeiro verificamos arquitetura do Windows
        //Verificar arquitetura do SO
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
        int nArquitetura = 0;

        String realArch = arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64") ? "64" : "32";
        System.out.println("PROCESSOR_ARCHITECTURE = " + realArch);
        if ("32".equals(realArch)){
            // SO Windows de 32 bits
            System.out.println("SO Windows 32 bits");
            nArquitetura = 32;

       } else {
            // SO Windows de 64 bits            
            System.out.println("SO Windows 64 bits");
            nArquitetura = 64;
       }
        
        //Conexão banco de dados
        DB db = new DB();
        try {
            Connection conn=db.dbConnect("jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=CI_ELETRONICO","sa","237recursos2211");
            //Connection conn=db.dbConnect("jdbc:sqlserver://172.22.8.17:1433;databaseName=Trans_SCC","sa","237recursos2211");
            if (null == conn){
                lblPaso01.setVisible(true);
                lblPaso01.setText("==> ERRO");
                bUpdateFeito = false;
                
            } else {    
                lblPaso01.setVisible(true);
                lblPaso01.setText("==> OK");

                //Salvamos na pasta Downloads o arquivo jar que atualizará o sistema
                String strUserHome = System.getProperty("user.home") + "\\Downloads\\";
                //String strUserDestination = "C:\\Repository\\Servidor_local_X4VC\\CI_EletronicoFX\\dist\\";   //Ambiente Desenvolvimento
                String strUserDestination = "C:\\Program Files\\CI_Eletronico\\app\\";  //Ambiente Produção
                

                String strFileName = "CI_Eletronico.jar";

                String strFileOrigem = strUserHome + strFileName;
                String strFileDestination = strUserDestination + strFileName;
                
                String strPathUpdateJar = strUserHome + "UpdateCiEletronica.jar";
                String strPathSqlJar = strUserHome + "\\lib\\sqljdbc42.jar";


                copyFile(strFileOrigem, strFileDestination, Boolean.TRUE);
                lblPaso02.setVisible(true);
                lblPaso02.setText("==> OK");
                
                
                //Deletando arquivos temporais
                
                bDeletadoCiEletronica = ApagarArquivoTemporal(strFileOrigem);   // Arquivo CI_Eletronico.jar
                if (bDeletadoCiEletronica){
                    bDeletadoSql = ApagarArquivoTemporal(strPathSqlJar);   // Arquivo sqljdbc42.jar
                } 
                if (bDeletadoCiEletronica && bDeletadoSql){
                    fJar = new File(strPathUpdateJar);                
                    fJar.deleteOnExit();
                    lblPaso03.setVisible(true);
                    lblPaso03.setText("==> OK");
                    
                    lblPaso04.setVisible(true);
                    lblPaso04.setText("==> OK");
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Atualização CI-Eletrônica");
                    alert.setHeaderText(null);
                    alert.setContentText("O sistema foi atualizado com sucesso");
                    alert.showAndWait();
                    
                    exit(0);
                    
                } else {
                    lblPaso03.setVisible(true);
                    lblPaso03.setText("==> ERRO");
                    
                    lblPaso04.setVisible(true);
                    lblPaso04.setText("==> OK Com ressalvas");
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Atualização CI-Eletrônica");
                    alert.setHeaderText(null);
                    alert.setContentText("O sistema foi atualizado. \n" + "OBS: Os arquivos temporais de atualização não puderam ser deletados");
                    alert.showAndWait();
                    
                    exit(0);
                    
                }   
            }
            
        
        }catch (Exception e)
        {
            e.printStackTrace();
            lblPaso01.setVisible(true);
            lblPaso01.setText("==> ERRO");
            bUpdateFeito = false;
        }
//        
//        try{
//    		
//    	   File afile =new File(strFileOrigem);
//           
//            if (afile.exists()){
//                
//                
//               
//                if(afile.renameTo(new File(strFileDestination))){
//                    System.out.println("File is moved successful!");
//                    btnFechar.setDisable(false);
//                    btnStartUpdate.setDisable(true);
//                }else{
//                    System.out.println("File is failed to move!");
//                    btnFechar.setDisable(true);
//               }    	                               
//            }else {
//                // Show the error message.
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Erro na atualização");
//                alert.setHeaderText(null);
//                alert.setContentText("Arquivo de atualização não foi encontrado.\nFavor contatar suporte ASSTI");
//                alert.showAndWait();
//                exit(0);
//                
//            }
//        } catch(Exception e){
//    		e.printStackTrace();
//                btnFechar.setDisable(true);
//                btnStartUpdate.setDisable(true);                
//        }
        
}
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lblPaso01.setVisible(false);
        lblPaso02.setVisible(false);
        lblPaso03.setVisible(false);
        lblPaso04.setVisible(false);
        btnStartUpdate.setVisible(true);
        btnFechar.setDisable(true);
        
        //btnStartUpdate.fire();
    }  
    
     public static void copyFile(String from, String to, Boolean overwrite) {

        try {
            File fromFile = new File(from);
            File toFile = new File(to);

            if (!fromFile.exists()) {
                throw new IOException("File not found: " + from);
            }
            if (!fromFile.isFile()) {
                throw new IOException("Can't copy directories: " + from);
            }
            if (!fromFile.canRead()) {
                throw new IOException("Can't read file: " + from);
            }

            if (toFile.isDirectory()) {
                toFile = new File(toFile, fromFile.getName());
            }

            if (toFile.exists() && !overwrite) {
                throw new IOException("File already exists.");
            } else {
                String parent = toFile.getParent();
                if (parent == null) {
                    parent = System.getProperty("user.dir");
                }
                File dir = new File(parent);
                if (!dir.exists()) {
                    throw new IOException("Destination directory does not exist: " + parent);
                }
                if (dir.isFile()) {
                    throw new IOException("Destination is not a valid directory: " + parent);
                }
                if (!dir.canWrite()) {
                    throw new IOException("Can't write on destination: " + parent);
                }
            }

            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {

                fis = new FileInputStream(fromFile);
                fos = new FileOutputStream(toFile);
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }

            } finally {
                if (from != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                      System.out.println(e);
                    }
                }
                if (to != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Problems when copying file.");
        }
    }
     public boolean ApagarArquivoTemporal(String strPath){
        File fJar = null;        
        boolean bDeletado = false;
        
        fJar = new File(strPath);                
        bDeletado = fJar.delete();
                
        if (bDeletado){
            return true;
        
        } else {
            return false;
        }
     }
    
}
class DB
{
    private static final int BUFFER_SIZE = 512;
        public DB() {}
 
        public Connection dbConnect(String db_connect_string,
           String db_userid, String db_password)
        {
                try
                {
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                        Connection conn = DriverManager.getConnection(
                          db_connect_string, db_userid, db_password);
 
                        System.out.println("connected");
                        
                        return conn;
                         
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                        return null;
                }
        }
 
        public void insertImage(Connection conn,String img)
        {
                int len;
                String query;
                PreparedStatement pstmt;
                 
                try
                {
                        File file = new File(img);
                        FileInputStream fis = new FileInputStream(file);
                        len = (int)file.length();
 
                        //query = ("insert into TB_ANEXO (ANEXO_NOME,ANEXO_TAMANHO,ANEXO_BLOB) VALUES(?,?,?)");
                        query = ("insert into TB_VERSOES_SISTEMA (VESI_VERSAO_JAR, VESI_NOME_JAR,VESI_TAMANHO_JAR,VESI_BLOB,VESI_ARQUITETURA_WINDOWS) VALUES(?,?,?,?,?)");
                        pstmt = conn.prepareStatement(query);
                        pstmt.setString(1,"1.2");
                        pstmt.setString(2,file.getName());
                        pstmt.setInt(3, len);
   
                        // Method used to insert a stream of bytes
                        pstmt.setBinaryStream(4, fis, len); 
                        pstmt.setInt(5, 64);
                        pstmt.executeUpdate();
 
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                }
        }
 
        public void getImageData(Connection conn, String strUserHome)
        {
                 
                 byte[] fileBytes;
                 String query;
                 String strNomeArquivo = "";
                 //String nomeArquivoSaved = img + ""
                 try
                 {
                         //query = "select ANEXO_NOME,ANEXO_BLOB from TB_ANEXO where ID_ANEXO = 1009";
                         //query = "select ANEXO_NOME,ANEXO_BLOB from TB_ANEXO where ID_ANEXO = 1009";
                         query = "select NOME_IMAGEM,FOTO from Entrega where ID = 149";
                         Statement state = conn.createStatement();
                         ResultSet rs = state.executeQuery(query);
                         if (rs.next()){  
                             Blob blob = rs.getBlob("FOTO");
                             strNomeArquivo = rs.getString("NOME_IMAGEM");
                             long nTamanho = blob.length();
                             //InputStream inputStream = blob.getBinaryStream();
                             InputStream inputStream = rs.getBinaryStream("FOTO");                             
                             OutputStream outputStream = new FileOutputStream(strUserHome + "BLOB//Trans_SCC//" + strNomeArquivo);
                             int bytesRead = -1;
                             byte[] buffer = new byte[BUFFER_SIZE];
                             while((bytesRead = inputStream.read(buffer)) != -1) {
                                 outputStream.write(buffer, 0, bytesRead);
                             }
                             inputStream.close();
                             outputStream.close();
                             System.out.println("File created and saved");
                             
//                            // Código original 
//                            fileBytes = rs.getBytes(1);
//                            //OutputStream targetFile = new FileOutputStream("C://Users//victorcmaf//Downloads//Trafego_New.JPG");
//                            //OutputStream targetFile = new FileOutputStream("C://Users//victorcmaf//Downloads//duvidasestrangeiros2_New.doc");
//                            OutputStream targetFile = new FileOutputStream("C://Users//victorcmaf//Downloads//GerarPDF_7102015182737_New.pdf");                                          
//
//                            targetFile.write(fileBytes);
//                            targetFile.close();
//                            // Fim do código original
                        }        
                         
                 }
                 catch (Exception e)
                 {
                         e.printStackTrace();
                 }
        }
};
