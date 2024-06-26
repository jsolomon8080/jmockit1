/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.coverage.lines;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import mockit.asm.controlFlow.Label;

/**
 * Coverage data gathered for a branch inside a line of source code.
 */
public final class BranchCoverageData extends LineSegmentData {
    private static final long serialVersionUID = 1003335601845442606L;
    static final BranchCoverageData INVALID = new BranchCoverageData(new Label());

    @Nonnull
    private transient Label label;

    BranchCoverageData(@Nonnull Label label) {
        this.label = label;
    }

    @Override
    public boolean isEmpty() {
        return empty || label.line == 0 && label.jumpTargetLine == 0;
    }

    @Nonnegative
    int getLine() {
        return label.jumpTargetLine == 0 ? label.line : label.jumpTargetLine;
    }

    private void readObject(@Nonnull ObjectInputStream in) throws IOException, ClassNotFoundException {
        label = new Label();
        label.line = in.readInt();
        in.defaultReadObject();
    }

    private void writeObject(@Nonnull ObjectOutputStream out) throws IOException {
        int line = getLine();
        out.writeInt(line);
        out.defaultWriteObject();
    }
}
