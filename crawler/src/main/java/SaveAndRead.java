import java.io.*;

public class SaveAndRead {

	public SaveAndRead(){
		super();
	}

	public void save(String path,String content) {
		File newFile = new File(path);//文件目录
		/** 创建文件，如果文件已存在则清空该文件 */
		if(newFile.exists()) {
			try {
				FileWriter fileWriter =new FileWriter(newFile);
				fileWriter.write("");
				fileWriter.flush();
				fileWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				newFile.createNewFile();
				System.out.println(path+"文件已创建");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*** 增加缓存 - 写 **/
		FileOutputStream fOutputStream=null;
		BufferedOutputStream buffOutputStream = null;	//防止无法关闭，放在try/catch外面
		try {
			fOutputStream=new FileOutputStream(path);
			buffOutputStream=new BufferedOutputStream(fOutputStream, 1024);//不要太大，迅雷也就几十M
			buffOutputStream.write(content.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				buffOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public String read(String path) {
		String content="";
		File file = new File(path);//文件目录
		/** 文件存在则直接读取，文件不存在则创建文件 */
		if(file.exists()) {//文件存在
			/*** 增加缓存 - 读 **/
			FileInputStream fInputStream = null;
			BufferedInputStream buffInputStream = null;	//防止无法关闭，放在try/catch外面
			try {
				fInputStream = new FileInputStream(path);
				buffInputStream = new BufferedInputStream(fInputStream,1024);	//不要太大，迅雷也就几十M
//				System.out.println(buffInputStream.available());	//inputStream.available()通过文件描述符获取文件的总大小
				byte[] bytes = new byte[buffInputStream.available()];
				buffInputStream.read(bytes);
//				System.out.println(new String(bytes));
				content=new String(bytes);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {	//先关缓存流，再关文件流
				try {
					buffInputStream.close();
					fInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			try {
				file.createNewFile();
//					System.out.println("文件已创建");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
}
