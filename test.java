
public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String s = "hello\0";
		System.out.println(s);
		s = getNullTerminatedString(s);
		System.out.println(s);
		
		String s2 = "qysvcus.doc\0asihcbqowu";
		System.out.println(s2);
		byte[]buffer = s2.getBytes();
		
		String s3 = new String(buffer);
		s3 = getNullTerminatedString(s3);
		System.out.println(s3);
	}
	
	
	private static String getNullTerminatedString(String info){
		int i = 0;
		String fileName = "";
		while(info.charAt(i) != '\0') {
			fileName += info.charAt(i);
			i++;
		}
		return fileName;
	}	

}
