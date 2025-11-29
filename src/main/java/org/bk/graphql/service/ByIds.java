package org.bk.graphql.service;

import java.util.List;

public record ByIds<T>(List<T> ids) {
}

