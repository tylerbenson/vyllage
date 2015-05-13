package documents.utilities;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class FindDuplicates {
	public <T> Set<T> findDuplicates(Collection<T> list) {

		Set<T> duplicates = new LinkedHashSet<T>();
		Set<T> uniques = new HashSet<T>();

		for (T t : list) {
			if (!uniques.add(t)) {
				duplicates.add(t);
			}
		}

		return duplicates;
	}
}
