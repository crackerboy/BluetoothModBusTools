package com.bluetooth.modbus.snrtools.uitls;

/**
 * �����ֺ��ֽڽ���ת����<br>
 * ����֪ʶ��<br>
 * �������ݴ洢���Դ��ģʽ�洢�ģ�<br>
 * byte: �ֽ����� ռ8λ������ 00000000<br>
 * char: �ַ����� ռ2���ֽ� 16λ������ byte[0] byte[1]<br>
 * int : �������� ռ4���ֽ� 32λ������ byte[0] byte[1] byte[2] byte[3]<br>
 * long: ���������� ռ8���ֽ� 64λ������ byte[0] byte[1] byte[2] byte[3] byte[4] byte[5]
 * byte[6] byte[7]<br>
 * float: ������(С��) ռ4���ֽ� 32λ������ byte[0] byte[1] byte[2] byte[3]<br>
 * double: ˫���ȸ�����(С��) ռ8���ֽ� 64λ������ byte[0] byte[1] byte[2] byte[3] byte[4]
 * byte[5] byte[6] byte[7]<br>
 */
public class NumberBytes {

	/**
	 * ��λ���Ҷ���
	 * 
	 * @param oriStr
	 *            ԭ�ַ���
	 * @param len
	 *            Ŀ���ַ�������
	 * @param alexin
	 *            ��λ�ַ�
	 * @return Ŀ���ַ���
	 */
	public static String padLeft(String oriStr, int len, char alexin) {
		String str = "";
		int strlen = oriStr.length();
		if (strlen < len) {
			for (int i = 0; i < len - strlen; i++) {
				str = str + alexin;
			}
		}
		str = str + oriStr;
		return str;
	}
	
	/**
	 * ��һ��2λ�ֽ�����ת��Ϊchar�ַ���<br>
	 * ע�⣬�����в�����ֽ����鳤�Ƚ����жϣ������б�֤�����������ȷ�ԡ�
	 * 
	 * @param b
	 *            �ֽ�����
	 * @return char�ַ�
	 */
	public static char bytesToChar(byte[] b) {
		char c = (char) ((b[0] << 8) & 0xFF00L);
		c |= (char) (b[1] & 0xFFL);
		return c;
	}

	/**
	 * ��һ��8λ�ֽ�����ת��Ϊ˫���ȸ�������<br>
	 * ע�⣬�����в�����ֽ����鳤�Ƚ����жϣ������б�֤�����������ȷ�ԡ�
	 * 
	 * @param b
	 *            �ֽ�����
	 * @return ˫���ȸ�����
	 */
	public static double bytesToDouble(byte[] b) {
		return Double.longBitsToDouble(bytesToLong(b));
	}

	/**
	 * ��һ��4λ�ֽ�����ת��Ϊ��������<br>
	 * ע�⣬�����в�����ֽ����鳤�Ƚ����жϣ������б�֤�����������ȷ�ԡ�
	 * 
	 * @param b
	 *            �ֽ�����
	 * @return ������
	 */
	public static float bytesToFloat(byte[] b) {
		return Float.intBitsToFloat(bytesToInt(b));
	}

	/**
	 * ��һ��4λ�ֽ�����ת��Ϊ4������<br>
	 * ע�⣬�����в�����ֽ����鳤�Ƚ����жϣ������б�֤�����������ȷ�ԡ�
	 * 
	 * @param b
	 *            �ֽ�����
	 * @return ����
	 */
	public static int bytesToInt(byte[] b) {
		int i = (b[0] << 24) & 0xFF000000;
		i |= (b[1] << 16) & 0xFF0000;
		i |= (b[2] << 8) & 0xFF00;
		i |= b[3] & 0xFF;
		return i;
	}

	/**
	 * ��һ��8λ�ֽ�����ת��Ϊ��������<br>
	 * ע�⣬�����в�����ֽ����鳤�Ƚ����жϣ������б�֤�����������ȷ�ԡ�
	 * 
	 * @param b
	 *            �ֽ�����
	 * @return ������
	 */
	public static long bytesToLong(byte[] b) {
		long l = ((long) b[0] << 56) & 0xFF00000000000000L;
		// �����ǿ��ת��Ϊlong����ôĬ�ϻᵱ��int���������32λ��ʧ
		l |= ((long) b[1] << 48) & 0xFF000000000000L;
		l |= ((long) b[2] << 40) & 0xFF0000000000L;
		l |= ((long) b[3] << 32) & 0xFF00000000L;
		l |= ((long) b[4] << 24) & 0xFF000000L;
		l |= ((long) b[5] << 16) & 0xFF0000L;
		l |= ((long) b[6] << 8) & 0xFF00L;
		l |= (long) b[7] & 0xFFL;
		return l;
	}

	/**
	 * ��һ��char�ַ�ת��λ�ֽ����飨2���ֽڣ���b[0]�洢��λ�ַ������
	 * 
	 * @param c
	 *            �ַ���java char 2���ֽڣ�
	 * @return �����ַ����ֽ�����
	 */
	public static byte[] charToBytes(char c) {
		byte[] b = new byte[8];
		b[0] = (byte) (c >>> 8);
		b[1] = (byte) c;
		return b;
	}

	/**
	 * ��һ��˫���ȸ�����ת��λ�ֽ����飨8���ֽڣ���b[0]�洢��λ�ַ������
	 * 
	 * @param d
	 *            ˫���ȸ�����
	 * @return ����˫���ȸ��������ֽ�����
	 */
	public static byte[] doubleToBytes(double d) {
		return longToBytes(Double.doubleToLongBits(d));
	}

	/**
	 * ��һ��������ת��Ϊ�ֽ����飨4���ֽڣ���b[0]�洢��λ�ַ������
	 * 
	 * @param f
	 *            ������
	 * @return �����������ֽ�����
	 */
	public static byte[] floatToBytes(float f) {
		return intToBytes(Float.floatToIntBits(f));
	}

	/**
	 * ��һ������ת��λ�ֽ�����(4���ֽ�)��b[0]�洢��λ�ַ������
	 * 
	 * @param i
	 *            ����
	 * @return �����������ֽ�����
	 */
	public static byte[] intToBytes(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i >>> 24);
		b[1] = (byte) (i >>> 16);
		b[2] = (byte) (i >>> 8);
		b[3] = (byte) i;
		return b;
	}

	/**
	 * ��һ��������ת��λ�ֽ�����(8���ֽ�)��b[0]�洢��λ�ַ������
	 * 
	 * @param l
	 *            ������
	 * @return �����������ֽ�����
	 */
	public static byte[] longToBytes(long l) {
		byte[] b = new byte[8];
		b[0] = (byte) (l >>> 56);
		b[1] = (byte) (l >>> 48);
		b[2] = (byte) (l >>> 40);
		b[3] = (byte) (l >>> 32);
		b[4] = (byte) (l >>> 24);
		b[5] = (byte) (l >>> 16);
		b[6] = (byte) (l >>> 8);
		b[7] = (byte) (l);
		return b;
	}

	// ��ʮ�������ַ���ת��Ϊfloat
	public static float hexStrToFloat(String str) {
		float result = 0;
		try {
			int temp = Integer.parseInt(str.trim(), 16);
			result = Float.intBitsToFloat(temp);
		} catch (NumberFormatException e) {
			long ltemp = Long.parseLong(str.trim(), 16);
			result = Float.intBitsToFloat((int) ltemp);
		}
		return result;
	}
	
	// ��ʮ�������ַ���ת��ΪLong
	public static long hexStrToLong(String str) {
		long result = 0;
		try {
			result = Long.parseLong(str.trim(), 16);
		} catch (NumberFormatException e) {
		}
		return result;
	}
}
