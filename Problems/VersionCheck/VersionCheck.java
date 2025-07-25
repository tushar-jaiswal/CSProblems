//  Author: Tushar Jaiswal
//  Creation Date: 2025-07-24

// You are given a list of dependency versions in the format XXX.YYY.ZZ, where each version may or may not support a specific functionality. Your task is as follows:

//  Part 1:
//  Given a list of versions, find and return the earliest version from the list that supports the functionality.

//  Part 2:
//  You are given many corner cases. Some of these test cases may contradict your previous assumptions. For example, a lower version might support the functionality while a higher version does not.

//  Your task is to:
//      Analyze each test case and determine the requirements or edge conditions it reveals.
//      Update your assumptions or logic based on observations from the test data.
//      Discuss and confirm newly observed constraints with the interviewer.
//      Print relevant information for debugging and understanding unexpected behaviors.

//  Note: The test environment may include distracting or irrelevant code simulating a production environment. Focus on the test data and functionality-related code to identify patterns and update your logic accordingly.

import java.util.*;

public class VersionCheck {
    private List<String> dependencyVersions;

    public VersionCheck(List<String> dependencyVersions) {
        this.dependencyVersions = new ArrayList<>(dependencyVersions);
        Collections.sort(this.dependencyVersions);
    }

    public boolean isFeatureSupportedInVersion(String feature, String version) {
        return feature.equals("A") &&
                (version.equals("103.003.03") || version.equals("203.030.02"));
    }

    public String findEarliestVersionSupportingFeature(String feature) {
        return binarySearch(feature);
    }

    private String binarySearch(String feature) {
        int low = 0;
        int high = dependencyVersions.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String midVersion = dependencyVersions.get(mid);

            if (isFeatureSupportedInVersion(feature, midVersion)) {
                if (mid - 1 >= 0 && isFeatureSupportedInVersion(feature, dependencyVersions.get(mid - 1))) {
                    high = mid - 1;
                } else {
                    return midVersion;
                }
            } else {
                low = mid + 1;
            }
        }

        throw new IllegalArgumentException("No supporting version found for feature " + feature);
    }

    public static void main(String[] args) {
        VersionCheck checker = new VersionCheck(Arrays.asList("103.003.02", "103.003.03", "203.030.02"));

        // Test 1: Feature A
        assert checker.findEarliestVersionSupportingFeature("A").equals("103.003.03");

        // Test 2: Feature B (should raise exception)
        try {
            checker.findEarliestVersionSupportingFeature("B");
            throw new AssertionError("Expected exception not thrown for feature B");
        } catch (IllegalArgumentException e) {
            assert e.getMessage().equals("No supporting version found for feature B");
        }

        System.out.println("All tests passed");
    }
}
