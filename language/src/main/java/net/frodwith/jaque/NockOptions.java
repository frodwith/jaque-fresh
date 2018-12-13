package net.frodwith.jaque;

import java.util.List;

import org.graalvm.options.OptionKey;
import org.graalvm.options.OptionCategory;
import org.graalvm.options.OptionDescriptor;

public final class NockOptions {
  public static final String JET_TREE_NAME = "nock.jets";
  public static final OptionKey<String> JET_TREE = new OptionKey<String>("");
  public static final String JET_TREE_HELP = "name of jet tree to use";

  public static final String COLD_HISTORY_NAME = "nock.cold";
  public static final OptionKey<String> COLD_HISTORY = new OptionKey<String>("");
  public static final String COLD_HISTORY_HELP = "name of cold history to use";

  public static final String FAST_NAME = "nock.fast";
  public static final OptionKey<Boolean> FAST = new OptionKey<Boolean>(true);
  public static final String FAST_HELP = "recognize %fast hints";

  public static final String HASH_NAME = "nock.hash";
  public static final OptionKey<Boolean> HASH = new OptionKey<Boolean>(true);
  public static final String HASH_HELP = "discover registrations by battery hash";

	public static void describe(List<OptionDescriptor> options) {
    options.add(OptionDescriptor.newBuilder(JET_TREE, JET_TREE_NAME)
                                .category(OptionCategory.USER)
                                .help(JET_TREE_HELP)
                                .build());

    options.add(OptionDescriptor.newBuilder(COLD_HISTORY, COLD_HISTORY_NAME)
                                .category(OptionCategory.USER)
                                .help(COLD_HISTORY_HELP)
                                .build());

    options.add(OptionDescriptor.newBuilder(FAST, FAST_NAME)
                                .category(OptionCategory.USER)
                                .help(FAST_HELP)
                                .build());

    options.add(OptionDescriptor.newBuilder(HASH, HASH_NAME)
                                .category(OptionCategory.USER)
                                .help(HASH_HELP)
                                .build());
	}
}
