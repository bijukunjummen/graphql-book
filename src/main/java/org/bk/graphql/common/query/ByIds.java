package org.bk.graphql.common.query;

import java.util.List;

public record ByIds<T>(List<T> ids) {
}

