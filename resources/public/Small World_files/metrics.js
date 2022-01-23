// Compiled by ClojureScript 1.10.520 {}
goog.provide('clj_fuzzy.metrics');
goog.require('cljs.core');
goog.require('clj_fuzzy.levenshtein');
goog.require('clj_fuzzy.dice');
goog.require('clj_fuzzy.match_rating');
goog.require('clj_fuzzy.jaccard');
goog.require('clj_fuzzy.hamming');
goog.require('clj_fuzzy.jaro_winkler');
goog.require('clj_fuzzy.tversky');
clj_fuzzy.metrics.levenshtein = clj_fuzzy.levenshtein.distance;
goog.exportSymbol('clj_fuzzy.metrics.levenshtein', clj_fuzzy.metrics.levenshtein);
clj_fuzzy.metrics.dice = clj_fuzzy.dice.coefficient;
goog.exportSymbol('clj_fuzzy.metrics.dice', clj_fuzzy.metrics.dice);
clj_fuzzy.metrics.sorensen = clj_fuzzy.dice.coefficient;
goog.exportSymbol('clj_fuzzy.metrics.sorensen', clj_fuzzy.metrics.sorensen);
clj_fuzzy.metrics.mra_comparison = clj_fuzzy.match_rating.mra_comparison;
goog.exportSymbol('clj_fuzzy.metrics.mra_comparison', clj_fuzzy.metrics.mra_comparison);
clj_fuzzy.metrics.jaccard = clj_fuzzy.jaccard.distance;
goog.exportSymbol('clj_fuzzy.metrics.jaccard', clj_fuzzy.metrics.jaccard);
clj_fuzzy.metrics.tanimoto = clj_fuzzy.jaccard.distance;
goog.exportSymbol('clj_fuzzy.metrics.tanimoto', clj_fuzzy.metrics.tanimoto);
clj_fuzzy.metrics.hamming = clj_fuzzy.hamming.distance;
goog.exportSymbol('clj_fuzzy.metrics.hamming', clj_fuzzy.metrics.hamming);
clj_fuzzy.metrics.jaro = clj_fuzzy.jaro_winkler.jaro;
goog.exportSymbol('clj_fuzzy.metrics.jaro', clj_fuzzy.metrics.jaro);
clj_fuzzy.metrics.jaro_winkler = clj_fuzzy.jaro_winkler.jaro_winkler;
goog.exportSymbol('clj_fuzzy.metrics.jaro_winkler', clj_fuzzy.metrics.jaro_winkler);
clj_fuzzy.metrics.tversky = clj_fuzzy.tversky.index;
goog.exportSymbol('clj_fuzzy.metrics.tversky', clj_fuzzy.metrics.tversky);

//# sourceMappingURL=metrics.js.map?rel=1642872176943
