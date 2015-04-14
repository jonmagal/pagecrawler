import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import br.gov.i3gov.core.CrawlerListener;
import br.gov.i3gov.crawler.links.BufferLinks;
import br.gov.i3gov.crawler.links.Link;
import br.gov.i3gov.crawler.links.LinkImpl;
import br.gov.i3gov.crawler.links.RAMQueueBufferLinks;
import br.gov.i3gov.crawler.tools.MultiThreadCrawler;
import br.gov.i3gov.crawler.tools.MultiThreadCrawlerImpl;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ProfileBuilder implements CrawlerListener{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        //String entrada_site = JOptionPane.showInputDialog("Insira o site");
        //String entrada_caminho = JOptionPane.showInputDialog("Insira o caminho");   jhjh
		TesteWebCrawler teste = new TesteWebCrawler();
		
		/* Cria link inicial para a navega��o web*/
		Link link = new LinkImpl();
		link.setUrl("http://www.columbia.edu/~rsb2162/");
		link.setDepth(new Long(0));
		
		/* Cria a buffer que cont�m os links a serem visitados */
		BufferLinks bufferLinks = new RAMQueueBufferLinks();
		
		/* Adiciona ao buffer o(s) link(s) iniciais para a navega��o na web */
		bufferLinks.push(link);
		
		/*express�o regular que indica quais urls devem ser navegadas. Nesse caso todas as urls deste dom�nio ser�o visitadas*/
		bufferLinks.addFiltro("http://www.columbia.edu/~rsb2162/.*");
		
		/* Criar o crawler de busca e adiciona a ele o buffer a ser utilizado */
		MultiThreadCrawler crawler = new MultiThreadCrawlerImpl();
		crawler.setBufferLinks(bufferLinks);
		
		/* Seta o n�mero de threads usadas pelo crawler */
		crawler.setMaxThreads(32);
		
		/* Adiciona a classe ouvinte que ser�  respons�vel por receber as p�ginas visitadas */
		crawler.addCrawlerListener(teste);
		
		/* Adiciona o tipo de p�gina a ser visitado pelo crawler. O default � que todos os tipos sejam visistados */
		crawler.addFilterContentType("text/html");
		
		/* Define profundidade */
		//crawler.setMaxDepth(1);
		 
		/* Inicia o crawler */
		crawler.startCrawler();
				
		try {
			Runtime r = Runtime.getRuntime();
			Process p;
			p = r.exec("python ./analyser.py ./dados.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
             
            String line;  
            while ((line = br.readLine()) != null) { 
            	try {

        	        File file = new File("./dadostratados.txt");
        	 
        	        // Cria o arquivo, caso n�o exista
        	        if(!file.exists()){
        	                file.createNewFile();
        	        }
        	 
        	        // O parametro 'true' � usado para escrever ao final do arquivo
        	        FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), false);
        	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        	        bufferWritter.write(line);
        			bufferWritter.flush();
        	        bufferWritter.close();
        		} catch(IOException e) {
        	        e.printStackTrace();
        		}
                //System.out.println(line);
            }  
            br.close();  
            p.waitFor();
        } catch (Exception e) {  
            e.printStackTrace();  
        }

	}
	
	
	/* Este m�todo deve ser implementado pela classe que escuta o crawler 
	 * 
	 * A cada p�gina processada pelo crawler este m�todo � chamado.
	 * */
	
	public void processPage(Page page, Link link) {
		
		/*Imprime a url, o tipo e o conte�do das p�ginas html visitadas*/
		
		//System.out.println(link.getUrl());
		//System.out.println(link.getContentType());
		//System.out.println(((HtmlPage)page).asText());
		//System.out.println("*********************************************");
	
		try {

	        File file = new File("./dados.txt");
	 
	        // Cria o arquivo, caso n�o exista
	        if(!file.exists()){
	                file.createNewFile();
	        }
	 
	        // O parametro 'true' � usado para escrever ao final do arquivo
	        FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        bufferWritter.write(link.getUrl()); // '\n' para quebrar a linha no arquivo de texto
	        bufferWritter.write(link.getContentType());
	        bufferWritter.write(((HtmlPage)page).asText());
	        //bufferWritter.write("*********************************************");
			bufferWritter.flush();
	        bufferWritter.close();
		} catch(IOException e) {
	        e.printStackTrace();
		}

		//String a = ((HtmlPage)page).asText();
		//System.out.println(a);

	}
}