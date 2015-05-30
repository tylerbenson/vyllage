package documents.utilities;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FindDuplicatesTest {

	@Test
	public void findsDuplicatesTest() {
		FindDuplicates finder = new FindDuplicates();
		List<Long> list = new ArrayList<>();
		list.add(42L);
		list.add(52L);
		list.add(52L);

		Assert.assertFalse(finder.findDuplicates(list).isEmpty());

	}

	@Test
	public void findsDuplicatesStringsTest() {
		FindDuplicates finder = new FindDuplicates();
		List<String> list = new ArrayList<>();
		list.add("aaa");
		list.add("aaa");
		list.add("bbb");

		Assert.assertFalse(finder.findDuplicates(list).isEmpty());

	}

	@Test
	public void noDuplicatesTest() {
		FindDuplicates finder = new FindDuplicates();
		List<Long> list = new ArrayList<>();
		list.add(42L);
		list.add(52L);

		Assert.assertTrue(finder.findDuplicates(list).isEmpty());

	}

}
