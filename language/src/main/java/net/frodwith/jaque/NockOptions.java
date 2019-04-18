package net.frodwith.jaque;

import java.util.List;

import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionCategory;
import org.graalvm.options.OptionDescriptor;

public final class NockOptions {
  public static final String MEMO_SIZE_NAME = "nock.memo";
  public static final OptionKey<Integer> MEMO_SIZE = new OptionKey<Integer>(1024);
  public static final String MEMO_SIZE_HELP = "size (n entries) of MEMO cache";

	public static void describe(List<OptionDescriptor> options) {
    options.add(OptionDescriptor.newBuilder(MEMO_SIZE, MEMO_SIZE_NAME)
                                .category(OptionCategory.USER)
                                .help(MEMO_SIZE_HELP)
                                .build());
	}
}
