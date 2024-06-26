/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.coverage;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import mockit.coverage.data.CoverageData;

final class AccretionFile {
    @Nonnull
    private final File outputFile;
    @Nonnull
    private final CoverageData newData;

    AccretionFile(@Nonnull String outputDir, @Nonnull CoverageData newData) {
        String parentDir = Configuration.getOrChooseOutputDirectory(outputDir);
        outputFile = new File(parentDir, "coverage.ser");

        newData.fillLastModifiedTimesForAllClassFiles();
        this.newData = newData;
    }

    void mergeDataFromExistingFileIfAny() throws IOException {
        if (outputFile.exists()) {
            CoverageData previousData = CoverageData.readDataFromFile(outputFile);
            newData.merge(previousData);
        }
    }

    void generate() throws IOException {
        newData.writeDataToFile(outputFile);
        System.out.println("JMockit: Coverage data written to " + outputFile.getCanonicalPath());
    }
}
