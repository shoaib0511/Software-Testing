package eecs4313a3;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.borg.common.EncryptionHelper;

public class ValidKeystoreWorstCaseBVTest {
	protected static EncryptionHelper eh;

	private final String EMPTY_STRING = "";
	private final String ALMOST_EMPTY_STRING = " ";
	private final String NOMINAL_STRING = "a sample message";

	/**
	 * Setup class to populate the store with a key
	 */
	@BeforeClass
	public static void setup() throws Exception {
		// Create a new store
		EncryptionHelper.createStore(EncryptionHelperTest.STORE_NAME, EncryptionHelperTest.STORE_PASS);

		// Add a key, manually
		EncryptionHelper.importKey(EncryptionHelperTest.STORE_NAME, EncryptionHelperTest.KEY,
				EncryptionHelperTest.KEYNAME, EncryptionHelperTest.STORE_PASS);

		// Set up the encryption helper
		ValidKeystoreWorstCaseBVTest.eh = new EncryptionHelper(EncryptionHelperTest.STORE_NAME,
				EncryptionHelperTest.STORE_PASS);

	}

	/**
	 * Method which generates a long string for use in tests
	 * @param  len Maximum length of the string
	 * @return     String of length `len`
	 */
	public static String generateLongString(long len) {
		final StringBuilder sb = new StringBuilder();

		for (long i = 0; i < len; i++) {
			sb.append("A");
		}

		return sb.toString();
	}

	/**
	 * Test a valid key using an empty string
	 */
	@Test
	public void testValidEmptyString() throws Exception {
		// Test smallest possible value, and ensure the encrypted
		// form is different than the plain form
		final String enc = ValidKeystoreWorstCaseBVTest.eh.encrypt(this.EMPTY_STRING,
				EncryptionHelperTest.KEYNAME);
		Assert.assertNotEquals(enc, this.EMPTY_STRING);
		Assert.assertEquals(this.EMPTY_STRING,
				ValidKeystoreWorstCaseBVTest.eh.decrypt(enc, EncryptionHelperTest.KEYNAME));
	}

	/**
	 * Test a valid key using a very small string
	 */
	@Test
	public void testValidSingleChar() throws Exception {
		// Test a single character encryption
		final String enc = ValidKeystoreWorstCaseBVTest.eh.encrypt(this.ALMOST_EMPTY_STRING,
				EncryptionHelperTest.KEYNAME);
		Assert.assertNotEquals(enc, this.ALMOST_EMPTY_STRING);
		Assert.assertEquals(this.ALMOST_EMPTY_STRING,
				ValidKeystoreWorstCaseBVTest.eh.decrypt(enc, EncryptionHelperTest.KEYNAME));
	}

	/**
	 * Test a valid key using a standard-length string
	 */
	@Test
	public void testValidNominal() throws Exception {
		// Test medium length encryption
		final String enc = ValidKeystoreWorstCaseBVTest.eh.encrypt(this.NOMINAL_STRING,
				EncryptionHelperTest.KEYNAME);
		Assert.assertNotEquals(enc, this.NOMINAL_STRING);
		Assert.assertEquals(this.NOMINAL_STRING,
				ValidKeystoreWorstCaseBVTest.eh.decrypt(enc, EncryptionHelperTest.KEYNAME));
	}

	/**
	 * Test a valid key against a large 4KB string
	 */
	@Test
	public void testValidLarge() throws Exception {
		// Test large length encryption
		final String longString = ValidKeystoreWorstCaseBVTest.generateLongString(4096);
		final String enc = ValidKeystoreWorstCaseBVTest.eh.encrypt(longString, EncryptionHelperTest.KEYNAME);
		Assert.assertNotEquals(longString, enc);
		Assert.assertEquals(longString,
				ValidKeystoreWorstCaseBVTest.eh.decrypt(enc, EncryptionHelperTest.KEYNAME));
	}

	/**
	 * Test a valid key using a binary string
	 */
	@Test
	public void testValidRawBytes() throws Exception {
		// Test some bytes that are less than 0x80 (String overflows there)
		final byte[] inp = { (byte) 0x00, (byte) 0x0c, (byte) 0x75, (byte) 0x00, (byte) 0x13 };
		final String s = new String(inp);
		final String enc = ValidKeystoreWorstCaseBVTest.eh.encrypt(s, EncryptionHelperTest.KEYNAME);
		final String dec = ValidKeystoreWorstCaseBVTest.eh.decrypt(enc, EncryptionHelperTest.KEYNAME);
		final byte[] out = dec.getBytes();

		boolean bytesMatch = true;
		for (int i = 0; i < inp.length; i++) {
			bytesMatch = bytesMatch & (inp[i] == out[i]);
		}

		Assert.assertTrue(bytesMatch);

	}
}
