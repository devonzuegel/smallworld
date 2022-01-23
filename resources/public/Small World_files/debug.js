// Compiled by ClojureScript 1.10.520 {}
goog.provide('reagent.debug');
goog.require('cljs.core');
reagent.debug.has_console = (typeof console !== 'undefined');
reagent.debug.tracking = false;
if((typeof reagent !== 'undefined') && (typeof reagent.debug !== 'undefined') && (typeof reagent.debug.warnings !== 'undefined')){
} else {
reagent.debug.warnings = cljs.core.atom.call(null,null);
}
if((typeof reagent !== 'undefined') && (typeof reagent.debug !== 'undefined') && (typeof reagent.debug.track_console !== 'undefined')){
} else {
reagent.debug.track_console = (function (){var o = ({});
o.warn = ((function (o){
return (function() { 
var G__29818__delegate = function (args){
return cljs.core.swap_BANG_.call(null,reagent.debug.warnings,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"warn","warn",-436710552)], null),cljs.core.conj,cljs.core.apply.call(null,cljs.core.str,args));
};
var G__29818 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__29819__i = 0, G__29819__a = new Array(arguments.length -  0);
while (G__29819__i < G__29819__a.length) {G__29819__a[G__29819__i] = arguments[G__29819__i + 0]; ++G__29819__i;}
  args = new cljs.core.IndexedSeq(G__29819__a,0,null);
} 
return G__29818__delegate.call(this,args);};
G__29818.cljs$lang$maxFixedArity = 0;
G__29818.cljs$lang$applyTo = (function (arglist__29820){
var args = cljs.core.seq(arglist__29820);
return G__29818__delegate(args);
});
G__29818.cljs$core$IFn$_invoke$arity$variadic = G__29818__delegate;
return G__29818;
})()
;})(o))
;

o.error = ((function (o){
return (function() { 
var G__29821__delegate = function (args){
return cljs.core.swap_BANG_.call(null,reagent.debug.warnings,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"error","error",-978969032)], null),cljs.core.conj,cljs.core.apply.call(null,cljs.core.str,args));
};
var G__29821 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__29822__i = 0, G__29822__a = new Array(arguments.length -  0);
while (G__29822__i < G__29822__a.length) {G__29822__a[G__29822__i] = arguments[G__29822__i + 0]; ++G__29822__i;}
  args = new cljs.core.IndexedSeq(G__29822__a,0,null);
} 
return G__29821__delegate.call(this,args);};
G__29821.cljs$lang$maxFixedArity = 0;
G__29821.cljs$lang$applyTo = (function (arglist__29823){
var args = cljs.core.seq(arglist__29823);
return G__29821__delegate(args);
});
G__29821.cljs$core$IFn$_invoke$arity$variadic = G__29821__delegate;
return G__29821;
})()
;})(o))
;

return o;
})();
}
reagent.debug.track_warnings = (function reagent$debug$track_warnings(f){
reagent.debug.tracking = true;

cljs.core.reset_BANG_.call(null,reagent.debug.warnings,null);

f.call(null);

var warns = cljs.core.deref.call(null,reagent.debug.warnings);
cljs.core.reset_BANG_.call(null,reagent.debug.warnings,null);

reagent.debug.tracking = false;

return warns;
});

//# sourceMappingURL=debug.js.map?rel=1642872176709
