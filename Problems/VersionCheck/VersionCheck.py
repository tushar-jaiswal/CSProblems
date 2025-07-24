# Author: Tushar Jaiswal
# Creation Date: 2025-07-24

# You are given a list of dependency versions in the format XXX.YYY.ZZ, where each version may or may not support a specific functionality. Your task is as follows:

# Part 1:
# Given a list of versions, find and return the earliest version from the list that supports the functionality.

# Part 2:
# You are given many corner cases. Some of these test cases may contradict your previous assumptions. For example, a lower version might support the functionality while a higher version does not.

# Your task is to:
#     Analyze each test case and determine the requirements or edge conditions it reveals.
#     Update your assumptions or logic based on observations from the test data.
#     Discuss and confirm newly observed constraints with the interviewer.
#     Print relevant information for debugging and understanding unexpected behaviors.

# Note: The test environment may include distracting or irrelevant code simulating a production environment. Focus on the test data and functionality-related code to identify patterns and update your logic accordingly.

import pytest

class VersionCheck:
    def __init__(self, dependency_versions: list):
        self.dependency_versions = dependency_versions
        dependency_versions.sort()
        
    def is_feature_supported_in_version(self, feature: str, version: str) -> bool:
        return True if (feature == "A" and (version == "103.003.03" or version == "203.030.02")) else False
    
    def find_earliest_version_supporting_feature(self, feature: str) -> str:
        return self.binary_search(feature)
    
    def binary_search(self, feature: str) -> str:
        size = len(self.dependency_versions)
        low = 0
        high = size - 1
        
        while (low <= high):
            mid = low + (high - low) // 2
            
            if (self.is_feature_supported_in_version(feature, self.dependency_versions[mid])):
                if (mid - 1 >= 0 and self.is_feature_supported_in_version(feature, self.dependency_versions[mid - 1])):
                    high = mid - 1
                else:
                    return self.dependency_versions[mid]
            else:
                low = mid + 1
        error = "No supporting version found for feature " + feature
        raise ValueError(error)

if __name__ == "__main__":
    checker = VersionCheck(["103.003.02", "103.003.03", "203.030.02"])
    
    assert checker.find_earliest_version_supporting_feature("A") == "103.003.03"
    
    with pytest.raises(ValueError, match = "No supporting version found for feature B"):checker.find_earliest_version_supporting_feature("B")
    print("All tests passed")
