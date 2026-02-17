package com.eeseka.shelflife.insight.domain.exception

class InsightItemNotFoundException(id: String) : RuntimeException("Insight item with ID $id not found")