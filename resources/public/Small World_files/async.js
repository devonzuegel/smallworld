// Compiled by ClojureScript 1.10.520 {}
goog.provide('cljs.core.async');
goog.require('cljs.core');
goog.require('cljs.core.async.impl.protocols');
goog.require('cljs.core.async.impl.channels');
goog.require('cljs.core.async.impl.buffers');
goog.require('cljs.core.async.impl.timers');
goog.require('cljs.core.async.impl.dispatch');
goog.require('cljs.core.async.impl.ioc_helpers');
cljs.core.async.fn_handler = (function cljs$core$async$fn_handler(var_args){
var G__33555 = arguments.length;
switch (G__33555) {
case 1:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1 = (function (f){
return cljs.core.async.fn_handler.call(null,f,true);
});

cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2 = (function (f,blockable){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async33556 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33556 = (function (f,blockable,meta33557){
this.f = f;
this.blockable = blockable;
this.meta33557 = meta33557;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async33556.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_33558,meta33557__$1){
var self__ = this;
var _33558__$1 = this;
return (new cljs.core.async.t_cljs$core$async33556(self__.f,self__.blockable,meta33557__$1));
});

cljs.core.async.t_cljs$core$async33556.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_33558){
var self__ = this;
var _33558__$1 = this;
return self__.meta33557;
});

cljs.core.async.t_cljs$core$async33556.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async33556.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
});

cljs.core.async.t_cljs$core$async33556.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.blockable;
});

cljs.core.async.t_cljs$core$async33556.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.f;
});

cljs.core.async.t_cljs$core$async33556.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"blockable","blockable",-28395259,null),new cljs.core.Symbol(null,"meta33557","meta33557",318224205,null)], null);
});

cljs.core.async.t_cljs$core$async33556.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async33556.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33556";

cljs.core.async.t_cljs$core$async33556.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async33556");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33556.
 */
cljs.core.async.__GT_t_cljs$core$async33556 = (function cljs$core$async$__GT_t_cljs$core$async33556(f__$1,blockable__$1,meta33557){
return (new cljs.core.async.t_cljs$core$async33556(f__$1,blockable__$1,meta33557));
});

}

return (new cljs.core.async.t_cljs$core$async33556(f,blockable,cljs.core.PersistentArrayMap.EMPTY));
});

cljs.core.async.fn_handler.cljs$lang$maxFixedArity = 2;

/**
 * Returns a fixed buffer of size n. When full, puts will block/park.
 */
cljs.core.async.buffer = (function cljs$core$async$buffer(n){
return cljs.core.async.impl.buffers.fixed_buffer.call(null,n);
});
/**
 * Returns a buffer of size n. When full, puts will complete but
 *   val will be dropped (no transfer).
 */
cljs.core.async.dropping_buffer = (function cljs$core$async$dropping_buffer(n){
return cljs.core.async.impl.buffers.dropping_buffer.call(null,n);
});
/**
 * Returns a buffer of size n. When full, puts will complete, and be
 *   buffered, but oldest elements in buffer will be dropped (not
 *   transferred).
 */
cljs.core.async.sliding_buffer = (function cljs$core$async$sliding_buffer(n){
return cljs.core.async.impl.buffers.sliding_buffer.call(null,n);
});
/**
 * Returns true if a channel created with buff will never block. That is to say,
 * puts into this buffer will never cause the buffer to be full. 
 */
cljs.core.async.unblocking_buffer_QMARK_ = (function cljs$core$async$unblocking_buffer_QMARK_(buff){
if((!((buff == null)))){
if(((false) || ((cljs.core.PROTOCOL_SENTINEL === buff.cljs$core$async$impl$protocols$UnblockingBuffer$)))){
return true;
} else {
if((!buff.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,buff);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,buff);
}
});
/**
 * Creates a channel with an optional buffer, an optional transducer (like (map f),
 *   (filter p) etc or a composition thereof), and an optional exception handler.
 *   If buf-or-n is a number, will create and use a fixed buffer of that size. If a
 *   transducer is supplied a buffer must be specified. ex-handler must be a
 *   fn of one argument - if an exception occurs during transformation it will be called
 *   with the thrown value as an argument, and any non-nil return value will be placed
 *   in the channel.
 */
cljs.core.async.chan = (function cljs$core$async$chan(var_args){
var G__33562 = arguments.length;
switch (G__33562) {
case 0:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.chan.call(null,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1 = (function (buf_or_n){
return cljs.core.async.chan.call(null,buf_or_n,null,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2 = (function (buf_or_n,xform){
return cljs.core.async.chan.call(null,buf_or_n,xform,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3 = (function (buf_or_n,xform,ex_handler){
var buf_or_n__$1 = ((cljs.core._EQ_.call(null,buf_or_n,(0)))?null:buf_or_n);
if(cljs.core.truth_(xform)){
if(cljs.core.truth_(buf_or_n__$1)){
} else {
throw (new Error(["Assert failed: ","buffer must be supplied when transducer is","\n","buf-or-n"].join('')));
}
} else {
}

return cljs.core.async.impl.channels.chan.call(null,((typeof buf_or_n__$1 === 'number')?cljs.core.async.buffer.call(null,buf_or_n__$1):buf_or_n__$1),xform,ex_handler);
});

cljs.core.async.chan.cljs$lang$maxFixedArity = 3;

/**
 * Creates a promise channel with an optional transducer, and an optional
 *   exception-handler. A promise channel can take exactly one value that consumers
 *   will receive. Once full, puts complete but val is dropped (no transfer).
 *   Consumers will block until either a value is placed in the channel or the
 *   channel is closed. See chan for the semantics of xform and ex-handler.
 */
cljs.core.async.promise_chan = (function cljs$core$async$promise_chan(var_args){
var G__33565 = arguments.length;
switch (G__33565) {
case 0:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.promise_chan.call(null,null);
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1 = (function (xform){
return cljs.core.async.promise_chan.call(null,xform,null);
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2 = (function (xform,ex_handler){
return cljs.core.async.chan.call(null,cljs.core.async.impl.buffers.promise_buffer.call(null),xform,ex_handler);
});

cljs.core.async.promise_chan.cljs$lang$maxFixedArity = 2;

/**
 * Returns a channel that will close after msecs
 */
cljs.core.async.timeout = (function cljs$core$async$timeout(msecs){
return cljs.core.async.impl.timers.timeout.call(null,msecs);
});
/**
 * takes a val from port. Must be called inside a (go ...) block. Will
 *   return nil if closed. Will park if nothing is available.
 *   Returns true unless port is already closed
 */
cljs.core.async._LT__BANG_ = (function cljs$core$async$_LT__BANG_(port){
throw (new Error("<! used not in (go ...) block"));
});
/**
 * Asynchronously takes a val from port, passing to fn1. Will pass nil
 * if closed. If on-caller? (default true) is true, and value is
 * immediately available, will call fn1 on calling thread.
 * Returns nil.
 */
cljs.core.async.take_BANG_ = (function cljs$core$async$take_BANG_(var_args){
var G__33568 = arguments.length;
switch (G__33568) {
case 2:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,fn1){
return cljs.core.async.take_BANG_.call(null,port,fn1,true);
});

cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,fn1,on_caller_QMARK_){
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.fn_handler.call(null,fn1));
if(cljs.core.truth_(ret)){
var val_33570 = cljs.core.deref.call(null,ret);
if(cljs.core.truth_(on_caller_QMARK_)){
fn1.call(null,val_33570);
} else {
cljs.core.async.impl.dispatch.run.call(null,((function (val_33570,ret){
return (function (){
return fn1.call(null,val_33570);
});})(val_33570,ret))
);
}
} else {
}

return null;
});

cljs.core.async.take_BANG_.cljs$lang$maxFixedArity = 3;

cljs.core.async.nop = (function cljs$core$async$nop(_){
return null;
});
cljs.core.async.fhnop = cljs.core.async.fn_handler.call(null,cljs.core.async.nop);
/**
 * puts a val into port. nil values are not allowed. Must be called
 *   inside a (go ...) block. Will park if no buffer space is available.
 *   Returns true unless port is already closed.
 */
cljs.core.async._GT__BANG_ = (function cljs$core$async$_GT__BANG_(port,val){
throw (new Error(">! used not in (go ...) block"));
});
/**
 * Asynchronously puts a val into port, calling fn0 (if supplied) when
 * complete. nil values are not allowed. Will throw if closed. If
 * on-caller? (default true) is true, and the put is immediately
 * accepted, will call fn0 on calling thread.  Returns nil.
 */
cljs.core.async.put_BANG_ = (function cljs$core$async$put_BANG_(var_args){
var G__33572 = arguments.length;
switch (G__33572) {
case 2:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,val){
var temp__5751__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fhnop);
if(cljs.core.truth_(temp__5751__auto__)){
var ret = temp__5751__auto__;
return cljs.core.deref.call(null,ret);
} else {
return true;
}
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,val,fn1){
return cljs.core.async.put_BANG_.call(null,port,val,fn1,true);
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4 = (function (port,val,fn1,on_caller_QMARK_){
var temp__5751__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fn_handler.call(null,fn1));
if(cljs.core.truth_(temp__5751__auto__)){
var retb = temp__5751__auto__;
var ret = cljs.core.deref.call(null,retb);
if(cljs.core.truth_(on_caller_QMARK_)){
fn1.call(null,ret);
} else {
cljs.core.async.impl.dispatch.run.call(null,((function (ret,retb,temp__5751__auto__){
return (function (){
return fn1.call(null,ret);
});})(ret,retb,temp__5751__auto__))
);
}

return ret;
} else {
return true;
}
});

cljs.core.async.put_BANG_.cljs$lang$maxFixedArity = 4;

cljs.core.async.close_BANG_ = (function cljs$core$async$close_BANG_(port){
return cljs.core.async.impl.protocols.close_BANG_.call(null,port);
});
cljs.core.async.random_array = (function cljs$core$async$random_array(n){
var a = (new Array(n));
var n__4607__auto___33574 = n;
var x_33575 = (0);
while(true){
if((x_33575 < n__4607__auto___33574)){
(a[x_33575] = (0));

var G__33576 = (x_33575 + (1));
x_33575 = G__33576;
continue;
} else {
}
break;
}

var i = (1);
while(true){
if(cljs.core._EQ_.call(null,i,n)){
return a;
} else {
var j = cljs.core.rand_int.call(null,i);
(a[i] = (a[j]));

(a[j] = i);

var G__33577 = (i + (1));
i = G__33577;
continue;
}
break;
}
});
cljs.core.async.alt_flag = (function cljs$core$async$alt_flag(){
var flag = cljs.core.atom.call(null,true);
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async33578 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33578 = (function (flag,meta33579){
this.flag = flag;
this.meta33579 = meta33579;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async33578.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (flag){
return (function (_33580,meta33579__$1){
var self__ = this;
var _33580__$1 = this;
return (new cljs.core.async.t_cljs$core$async33578(self__.flag,meta33579__$1));
});})(flag))
;

cljs.core.async.t_cljs$core$async33578.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (flag){
return (function (_33580){
var self__ = this;
var _33580__$1 = this;
return self__.meta33579;
});})(flag))
;

cljs.core.async.t_cljs$core$async33578.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async33578.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.deref.call(null,self__.flag);
});})(flag))
;

cljs.core.async.t_cljs$core$async33578.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
return true;
});})(flag))
;

cljs.core.async.t_cljs$core$async33578.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.flag,null);

return true;
});})(flag))
;

cljs.core.async.t_cljs$core$async33578.getBasis = ((function (flag){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"meta33579","meta33579",-127214103,null)], null);
});})(flag))
;

cljs.core.async.t_cljs$core$async33578.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async33578.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33578";

cljs.core.async.t_cljs$core$async33578.cljs$lang$ctorPrWriter = ((function (flag){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async33578");
});})(flag))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33578.
 */
cljs.core.async.__GT_t_cljs$core$async33578 = ((function (flag){
return (function cljs$core$async$alt_flag_$___GT_t_cljs$core$async33578(flag__$1,meta33579){
return (new cljs.core.async.t_cljs$core$async33578(flag__$1,meta33579));
});})(flag))
;

}

return (new cljs.core.async.t_cljs$core$async33578(flag,cljs.core.PersistentArrayMap.EMPTY));
});
cljs.core.async.alt_handler = (function cljs$core$async$alt_handler(flag,cb){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async33581 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async33581 = (function (flag,cb,meta33582){
this.flag = flag;
this.cb = cb;
this.meta33582 = meta33582;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async33581.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_33583,meta33582__$1){
var self__ = this;
var _33583__$1 = this;
return (new cljs.core.async.t_cljs$core$async33581(self__.flag,self__.cb,meta33582__$1));
});

cljs.core.async.t_cljs$core$async33581.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_33583){
var self__ = this;
var _33583__$1 = this;
return self__.meta33582;
});

cljs.core.async.t_cljs$core$async33581.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async33581.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.flag);
});

cljs.core.async.t_cljs$core$async33581.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
});

cljs.core.async.t_cljs$core$async33581.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.async.impl.protocols.commit.call(null,self__.flag);

return self__.cb;
});

cljs.core.async.t_cljs$core$async33581.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"cb","cb",-2064487928,null),new cljs.core.Symbol(null,"meta33582","meta33582",155590531,null)], null);
});

cljs.core.async.t_cljs$core$async33581.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async33581.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async33581";

cljs.core.async.t_cljs$core$async33581.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async33581");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async33581.
 */
cljs.core.async.__GT_t_cljs$core$async33581 = (function cljs$core$async$alt_handler_$___GT_t_cljs$core$async33581(flag__$1,cb__$1,meta33582){
return (new cljs.core.async.t_cljs$core$async33581(flag__$1,cb__$1,meta33582));
});

}

return (new cljs.core.async.t_cljs$core$async33581(flag,cb,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * returns derefable [val port] if immediate, nil if enqueued
 */
cljs.core.async.do_alts = (function cljs$core$async$do_alts(fret,ports,opts){
var flag = cljs.core.async.alt_flag.call(null);
var n = cljs.core.count.call(null,ports);
var idxs = cljs.core.async.random_array.call(null,n);
var priority = new cljs.core.Keyword(null,"priority","priority",1431093715).cljs$core$IFn$_invoke$arity$1(opts);
var ret = (function (){var i = (0);
while(true){
if((i < n)){
var idx = (cljs.core.truth_(priority)?i:(idxs[i]));
var port = cljs.core.nth.call(null,ports,idx);
var wport = ((cljs.core.vector_QMARK_.call(null,port))?port.call(null,(0)):null);
var vbox = (cljs.core.truth_(wport)?(function (){var val = port.call(null,(1));
return cljs.core.async.impl.protocols.put_BANG_.call(null,wport,val,cljs.core.async.alt_handler.call(null,flag,((function (i,val,idx,port,wport,flag,n,idxs,priority){
return (function (p1__33584_SHARP_){
return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__33584_SHARP_,wport], null));
});})(i,val,idx,port,wport,flag,n,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.alt_handler.call(null,flag,((function (i,idx,port,wport,flag,n,idxs,priority){
return (function (p1__33585_SHARP_){
return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__33585_SHARP_,port], null));
});})(i,idx,port,wport,flag,n,idxs,priority))
)));
if(cljs.core.truth_(vbox)){
return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.deref.call(null,vbox),(function (){var or__4131__auto__ = wport;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return port;
}
})()], null));
} else {
var G__33586 = (i + (1));
i = G__33586;
continue;
}
} else {
return null;
}
break;
}
})();
var or__4131__auto__ = ret;
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
if(cljs.core.contains_QMARK_.call(null,opts,new cljs.core.Keyword(null,"default","default",-1987822328))){
var temp__5753__auto__ = (function (){var and__4120__auto__ = cljs.core.async.impl.protocols.active_QMARK_.call(null,flag);
if(cljs.core.truth_(and__4120__auto__)){
return cljs.core.async.impl.protocols.commit.call(null,flag);
} else {
return and__4120__auto__;
}
})();
if(cljs.core.truth_(temp__5753__auto__)){
var got = temp__5753__auto__;
return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"default","default",-1987822328).cljs$core$IFn$_invoke$arity$1(opts),new cljs.core.Keyword(null,"default","default",-1987822328)], null));
} else {
return null;
}
} else {
return null;
}
}
});
/**
 * Completes at most one of several channel operations. Must be called
 * inside a (go ...) block. ports is a vector of channel endpoints,
 * which can be either a channel to take from or a vector of
 *   [channel-to-put-to val-to-put], in any combination. Takes will be
 *   made as if by <!, and puts will be made as if by >!. Unless
 *   the :priority option is true, if more than one port operation is
 *   ready a non-deterministic choice will be made. If no operation is
 *   ready and a :default value is supplied, [default-val :default] will
 *   be returned, otherwise alts! will park until the first operation to
 *   become ready completes. Returns [val port] of the completed
 *   operation, where val is the value taken for takes, and a
 *   boolean (true unless already closed, as per put!) for puts.
 * 
 *   opts are passed as :key val ... Supported options:
 * 
 *   :default val - the value to use if none of the operations are immediately ready
 *   :priority true - (default nil) when true, the operations will be tried in order.
 * 
 *   Note: there is no guarantee that the port exps or val exprs will be
 *   used, nor in what order should they be, so they should not be
 *   depended upon for side effects.
 */
cljs.core.async.alts_BANG_ = (function cljs$core$async$alts_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___33592 = arguments.length;
var i__4731__auto___33593 = (0);
while(true){
if((i__4731__auto___33593 < len__4730__auto___33592)){
args__4736__auto__.push((arguments[i__4731__auto___33593]));

var G__33594 = (i__4731__auto___33593 + (1));
i__4731__auto___33593 = G__33594;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((1) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((1)),(0),null)):null);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__4737__auto__);
});

cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ports,p__33589){
var map__33590 = p__33589;
var map__33590__$1 = (((((!((map__33590 == null))))?(((((map__33590.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__33590.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__33590):map__33590);
var opts = map__33590__$1;
throw (new Error("alts! used not in (go ...) block"));
});

cljs.core.async.alts_BANG_.cljs$lang$maxFixedArity = (1);

/** @this {Function} */
cljs.core.async.alts_BANG_.cljs$lang$applyTo = (function (seq33587){
var G__33588 = cljs.core.first.call(null,seq33587);
var seq33587__$1 = cljs.core.next.call(null,seq33587);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__33588,seq33587__$1);
});

/**
 * Puts a val into port if it's possible to do so immediately.
 *   nil values are not allowed. Never blocks. Returns true if offer succeeds.
 */
cljs.core.async.offer_BANG_ = (function cljs$core$async$offer_BANG_(port,val){
var ret = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fn_handler.call(null,cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref.call(null,ret);
} else {
return null;
}
});
/**
 * Takes a val from port if it's possible to do so immediately.
 *   Never blocks. Returns value if successful, nil otherwise.
 */
cljs.core.async.poll_BANG_ = (function cljs$core$async$poll_BANG_(port){
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.fn_handler.call(null,cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref.call(null,ret);
} else {
return null;
}
});
/**
 * Takes elements from the from channel and supplies them to the to
 * channel. By default, the to channel will be closed when the from
 * channel closes, but can be determined by the close?  parameter. Will
 * stop consuming the from channel if the to channel closes
 */
cljs.core.async.pipe = (function cljs$core$async$pipe(var_args){
var G__33596 = arguments.length;
switch (G__33596) {
case 2:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2 = (function (from,to){
return cljs.core.async.pipe.call(null,from,to,true);
});

cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3 = (function (from,to,close_QMARK_){
var c__33495__auto___33642 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___33642){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___33642){
return (function (state_33620){
var state_val_33621 = (state_33620[(1)]);
if((state_val_33621 === (7))){
var inst_33616 = (state_33620[(2)]);
var state_33620__$1 = state_33620;
var statearr_33622_33643 = state_33620__$1;
(statearr_33622_33643[(2)] = inst_33616);

(statearr_33622_33643[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (1))){
var state_33620__$1 = state_33620;
var statearr_33623_33644 = state_33620__$1;
(statearr_33623_33644[(2)] = null);

(statearr_33623_33644[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (4))){
var inst_33599 = (state_33620[(7)]);
var inst_33599__$1 = (state_33620[(2)]);
var inst_33600 = (inst_33599__$1 == null);
var state_33620__$1 = (function (){var statearr_33624 = state_33620;
(statearr_33624[(7)] = inst_33599__$1);

return statearr_33624;
})();
if(cljs.core.truth_(inst_33600)){
var statearr_33625_33645 = state_33620__$1;
(statearr_33625_33645[(1)] = (5));

} else {
var statearr_33626_33646 = state_33620__$1;
(statearr_33626_33646[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (13))){
var state_33620__$1 = state_33620;
var statearr_33627_33647 = state_33620__$1;
(statearr_33627_33647[(2)] = null);

(statearr_33627_33647[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (6))){
var inst_33599 = (state_33620[(7)]);
var state_33620__$1 = state_33620;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_33620__$1,(11),to,inst_33599);
} else {
if((state_val_33621 === (3))){
var inst_33618 = (state_33620[(2)]);
var state_33620__$1 = state_33620;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33620__$1,inst_33618);
} else {
if((state_val_33621 === (12))){
var state_33620__$1 = state_33620;
var statearr_33628_33648 = state_33620__$1;
(statearr_33628_33648[(2)] = null);

(statearr_33628_33648[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (2))){
var state_33620__$1 = state_33620;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33620__$1,(4),from);
} else {
if((state_val_33621 === (11))){
var inst_33609 = (state_33620[(2)]);
var state_33620__$1 = state_33620;
if(cljs.core.truth_(inst_33609)){
var statearr_33629_33649 = state_33620__$1;
(statearr_33629_33649[(1)] = (12));

} else {
var statearr_33630_33650 = state_33620__$1;
(statearr_33630_33650[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (9))){
var state_33620__$1 = state_33620;
var statearr_33631_33651 = state_33620__$1;
(statearr_33631_33651[(2)] = null);

(statearr_33631_33651[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (5))){
var state_33620__$1 = state_33620;
if(cljs.core.truth_(close_QMARK_)){
var statearr_33632_33652 = state_33620__$1;
(statearr_33632_33652[(1)] = (8));

} else {
var statearr_33633_33653 = state_33620__$1;
(statearr_33633_33653[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (14))){
var inst_33614 = (state_33620[(2)]);
var state_33620__$1 = state_33620;
var statearr_33634_33654 = state_33620__$1;
(statearr_33634_33654[(2)] = inst_33614);

(statearr_33634_33654[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (10))){
var inst_33606 = (state_33620[(2)]);
var state_33620__$1 = state_33620;
var statearr_33635_33655 = state_33620__$1;
(statearr_33635_33655[(2)] = inst_33606);

(statearr_33635_33655[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33621 === (8))){
var inst_33603 = cljs.core.async.close_BANG_.call(null,to);
var state_33620__$1 = state_33620;
var statearr_33636_33656 = state_33620__$1;
(statearr_33636_33656[(2)] = inst_33603);

(statearr_33636_33656[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___33642))
;
return ((function (switch__33350__auto__,c__33495__auto___33642){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_33637 = [null,null,null,null,null,null,null,null];
(statearr_33637[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_33637[(1)] = (1));

return statearr_33637;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_33620){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33620);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33638){if((e33638 instanceof Object)){
var ex__33354__auto__ = e33638;
var statearr_33639_33657 = state_33620;
(statearr_33639_33657[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33620);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33638;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__33658 = state_33620;
state_33620 = G__33658;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_33620){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_33620);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___33642))
})();
var state__33497__auto__ = (function (){var statearr_33640 = f__33496__auto__.call(null);
(statearr_33640[(6)] = c__33495__auto___33642);

return statearr_33640;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___33642))
);


return to;
});

cljs.core.async.pipe.cljs$lang$maxFixedArity = 3;

cljs.core.async.pipeline_STAR_ = (function cljs$core$async$pipeline_STAR_(n,to,xf,from,close_QMARK_,ex_handler,type){
if((n > (0))){
} else {
throw (new Error("Assert failed: (pos? n)"));
}

var jobs = cljs.core.async.chan.call(null,n);
var results = cljs.core.async.chan.call(null,n);
var process__$1 = ((function (jobs,results){
return (function (p__33659){
var vec__33660 = p__33659;
var v = cljs.core.nth.call(null,vec__33660,(0),null);
var p = cljs.core.nth.call(null,vec__33660,(1),null);
var job = vec__33660;
if((job == null)){
cljs.core.async.close_BANG_.call(null,results);

return null;
} else {
var res = cljs.core.async.chan.call(null,(1),xf,ex_handler);
var c__33495__auto___33831 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___33831,res,vec__33660,v,p,job,jobs,results){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___33831,res,vec__33660,v,p,job,jobs,results){
return (function (state_33667){
var state_val_33668 = (state_33667[(1)]);
if((state_val_33668 === (1))){
var state_33667__$1 = state_33667;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_33667__$1,(2),res,v);
} else {
if((state_val_33668 === (2))){
var inst_33664 = (state_33667[(2)]);
var inst_33665 = cljs.core.async.close_BANG_.call(null,res);
var state_33667__$1 = (function (){var statearr_33669 = state_33667;
(statearr_33669[(7)] = inst_33664);

return statearr_33669;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33667__$1,inst_33665);
} else {
return null;
}
}
});})(c__33495__auto___33831,res,vec__33660,v,p,job,jobs,results))
;
return ((function (switch__33350__auto__,c__33495__auto___33831,res,vec__33660,v,p,job,jobs,results){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0 = (function (){
var statearr_33670 = [null,null,null,null,null,null,null,null];
(statearr_33670[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__);

(statearr_33670[(1)] = (1));

return statearr_33670;
});
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1 = (function (state_33667){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33667);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33671){if((e33671 instanceof Object)){
var ex__33354__auto__ = e33671;
var statearr_33672_33832 = state_33667;
(statearr_33672_33832[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33667);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33671;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__33833 = state_33667;
state_33667 = G__33833;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = function(state_33667){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1.call(this,state_33667);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___33831,res,vec__33660,v,p,job,jobs,results))
})();
var state__33497__auto__ = (function (){var statearr_33673 = f__33496__auto__.call(null);
(statearr_33673[(6)] = c__33495__auto___33831);

return statearr_33673;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___33831,res,vec__33660,v,p,job,jobs,results))
);


cljs.core.async.put_BANG_.call(null,p,res);

return true;
}
});})(jobs,results))
;
var async = ((function (jobs,results,process__$1){
return (function (p__33674){
var vec__33675 = p__33674;
var v = cljs.core.nth.call(null,vec__33675,(0),null);
var p = cljs.core.nth.call(null,vec__33675,(1),null);
var job = vec__33675;
if((job == null)){
cljs.core.async.close_BANG_.call(null,results);

return null;
} else {
var res = cljs.core.async.chan.call(null,(1));
xf.call(null,v,res);

cljs.core.async.put_BANG_.call(null,p,res);

return true;
}
});})(jobs,results,process__$1))
;
var n__4607__auto___33834 = n;
var __33835 = (0);
while(true){
if((__33835 < n__4607__auto___33834)){
var G__33678_33836 = type;
var G__33678_33837__$1 = (((G__33678_33836 instanceof cljs.core.Keyword))?G__33678_33836.fqn:null);
switch (G__33678_33837__$1) {
case "compute":
var c__33495__auto___33839 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (__33835,c__33495__auto___33839,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (__33835,c__33495__auto___33839,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async){
return (function (state_33691){
var state_val_33692 = (state_33691[(1)]);
if((state_val_33692 === (1))){
var state_33691__$1 = state_33691;
var statearr_33693_33840 = state_33691__$1;
(statearr_33693_33840[(2)] = null);

(statearr_33693_33840[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33692 === (2))){
var state_33691__$1 = state_33691;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33691__$1,(4),jobs);
} else {
if((state_val_33692 === (3))){
var inst_33689 = (state_33691[(2)]);
var state_33691__$1 = state_33691;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33691__$1,inst_33689);
} else {
if((state_val_33692 === (4))){
var inst_33681 = (state_33691[(2)]);
var inst_33682 = process__$1.call(null,inst_33681);
var state_33691__$1 = state_33691;
if(cljs.core.truth_(inst_33682)){
var statearr_33694_33841 = state_33691__$1;
(statearr_33694_33841[(1)] = (5));

} else {
var statearr_33695_33842 = state_33691__$1;
(statearr_33695_33842[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33692 === (5))){
var state_33691__$1 = state_33691;
var statearr_33696_33843 = state_33691__$1;
(statearr_33696_33843[(2)] = null);

(statearr_33696_33843[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33692 === (6))){
var state_33691__$1 = state_33691;
var statearr_33697_33844 = state_33691__$1;
(statearr_33697_33844[(2)] = null);

(statearr_33697_33844[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33692 === (7))){
var inst_33687 = (state_33691[(2)]);
var state_33691__$1 = state_33691;
var statearr_33698_33845 = state_33691__$1;
(statearr_33698_33845[(2)] = inst_33687);

(statearr_33698_33845[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__33835,c__33495__auto___33839,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async))
;
return ((function (__33835,switch__33350__auto__,c__33495__auto___33839,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0 = (function (){
var statearr_33699 = [null,null,null,null,null,null,null];
(statearr_33699[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__);

(statearr_33699[(1)] = (1));

return statearr_33699;
});
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1 = (function (state_33691){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33691);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33700){if((e33700 instanceof Object)){
var ex__33354__auto__ = e33700;
var statearr_33701_33846 = state_33691;
(statearr_33701_33846[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33691);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33700;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__33847 = state_33691;
state_33691 = G__33847;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = function(state_33691){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1.call(this,state_33691);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__;
})()
;})(__33835,switch__33350__auto__,c__33495__auto___33839,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async))
})();
var state__33497__auto__ = (function (){var statearr_33702 = f__33496__auto__.call(null);
(statearr_33702[(6)] = c__33495__auto___33839);

return statearr_33702;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(__33835,c__33495__auto___33839,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async))
);


break;
case "async":
var c__33495__auto___33848 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (__33835,c__33495__auto___33848,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (__33835,c__33495__auto___33848,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async){
return (function (state_33715){
var state_val_33716 = (state_33715[(1)]);
if((state_val_33716 === (1))){
var state_33715__$1 = state_33715;
var statearr_33717_33849 = state_33715__$1;
(statearr_33717_33849[(2)] = null);

(statearr_33717_33849[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33716 === (2))){
var state_33715__$1 = state_33715;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33715__$1,(4),jobs);
} else {
if((state_val_33716 === (3))){
var inst_33713 = (state_33715[(2)]);
var state_33715__$1 = state_33715;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33715__$1,inst_33713);
} else {
if((state_val_33716 === (4))){
var inst_33705 = (state_33715[(2)]);
var inst_33706 = async.call(null,inst_33705);
var state_33715__$1 = state_33715;
if(cljs.core.truth_(inst_33706)){
var statearr_33718_33850 = state_33715__$1;
(statearr_33718_33850[(1)] = (5));

} else {
var statearr_33719_33851 = state_33715__$1;
(statearr_33719_33851[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33716 === (5))){
var state_33715__$1 = state_33715;
var statearr_33720_33852 = state_33715__$1;
(statearr_33720_33852[(2)] = null);

(statearr_33720_33852[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33716 === (6))){
var state_33715__$1 = state_33715;
var statearr_33721_33853 = state_33715__$1;
(statearr_33721_33853[(2)] = null);

(statearr_33721_33853[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33716 === (7))){
var inst_33711 = (state_33715[(2)]);
var state_33715__$1 = state_33715;
var statearr_33722_33854 = state_33715__$1;
(statearr_33722_33854[(2)] = inst_33711);

(statearr_33722_33854[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__33835,c__33495__auto___33848,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async))
;
return ((function (__33835,switch__33350__auto__,c__33495__auto___33848,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0 = (function (){
var statearr_33723 = [null,null,null,null,null,null,null];
(statearr_33723[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__);

(statearr_33723[(1)] = (1));

return statearr_33723;
});
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1 = (function (state_33715){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33715);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33724){if((e33724 instanceof Object)){
var ex__33354__auto__ = e33724;
var statearr_33725_33855 = state_33715;
(statearr_33725_33855[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33715);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33724;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__33856 = state_33715;
state_33715 = G__33856;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = function(state_33715){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1.call(this,state_33715);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__;
})()
;})(__33835,switch__33350__auto__,c__33495__auto___33848,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async))
})();
var state__33497__auto__ = (function (){var statearr_33726 = f__33496__auto__.call(null);
(statearr_33726[(6)] = c__33495__auto___33848);

return statearr_33726;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(__33835,c__33495__auto___33848,G__33678_33836,G__33678_33837__$1,n__4607__auto___33834,jobs,results,process__$1,async))
);


break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__33678_33837__$1)].join('')));

}

var G__33857 = (__33835 + (1));
__33835 = G__33857;
continue;
} else {
}
break;
}

var c__33495__auto___33858 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___33858,jobs,results,process__$1,async){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___33858,jobs,results,process__$1,async){
return (function (state_33748){
var state_val_33749 = (state_33748[(1)]);
if((state_val_33749 === (7))){
var inst_33744 = (state_33748[(2)]);
var state_33748__$1 = state_33748;
var statearr_33750_33859 = state_33748__$1;
(statearr_33750_33859[(2)] = inst_33744);

(statearr_33750_33859[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33749 === (1))){
var state_33748__$1 = state_33748;
var statearr_33751_33860 = state_33748__$1;
(statearr_33751_33860[(2)] = null);

(statearr_33751_33860[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33749 === (4))){
var inst_33729 = (state_33748[(7)]);
var inst_33729__$1 = (state_33748[(2)]);
var inst_33730 = (inst_33729__$1 == null);
var state_33748__$1 = (function (){var statearr_33752 = state_33748;
(statearr_33752[(7)] = inst_33729__$1);

return statearr_33752;
})();
if(cljs.core.truth_(inst_33730)){
var statearr_33753_33861 = state_33748__$1;
(statearr_33753_33861[(1)] = (5));

} else {
var statearr_33754_33862 = state_33748__$1;
(statearr_33754_33862[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33749 === (6))){
var inst_33734 = (state_33748[(8)]);
var inst_33729 = (state_33748[(7)]);
var inst_33734__$1 = cljs.core.async.chan.call(null,(1));
var inst_33735 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_33736 = [inst_33729,inst_33734__$1];
var inst_33737 = (new cljs.core.PersistentVector(null,2,(5),inst_33735,inst_33736,null));
var state_33748__$1 = (function (){var statearr_33755 = state_33748;
(statearr_33755[(8)] = inst_33734__$1);

return statearr_33755;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_33748__$1,(8),jobs,inst_33737);
} else {
if((state_val_33749 === (3))){
var inst_33746 = (state_33748[(2)]);
var state_33748__$1 = state_33748;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33748__$1,inst_33746);
} else {
if((state_val_33749 === (2))){
var state_33748__$1 = state_33748;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33748__$1,(4),from);
} else {
if((state_val_33749 === (9))){
var inst_33741 = (state_33748[(2)]);
var state_33748__$1 = (function (){var statearr_33756 = state_33748;
(statearr_33756[(9)] = inst_33741);

return statearr_33756;
})();
var statearr_33757_33863 = state_33748__$1;
(statearr_33757_33863[(2)] = null);

(statearr_33757_33863[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33749 === (5))){
var inst_33732 = cljs.core.async.close_BANG_.call(null,jobs);
var state_33748__$1 = state_33748;
var statearr_33758_33864 = state_33748__$1;
(statearr_33758_33864[(2)] = inst_33732);

(statearr_33758_33864[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33749 === (8))){
var inst_33734 = (state_33748[(8)]);
var inst_33739 = (state_33748[(2)]);
var state_33748__$1 = (function (){var statearr_33759 = state_33748;
(statearr_33759[(10)] = inst_33739);

return statearr_33759;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_33748__$1,(9),results,inst_33734);
} else {
return null;
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___33858,jobs,results,process__$1,async))
;
return ((function (switch__33350__auto__,c__33495__auto___33858,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0 = (function (){
var statearr_33760 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_33760[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__);

(statearr_33760[(1)] = (1));

return statearr_33760;
});
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1 = (function (state_33748){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33748);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33761){if((e33761 instanceof Object)){
var ex__33354__auto__ = e33761;
var statearr_33762_33865 = state_33748;
(statearr_33762_33865[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33748);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33761;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__33866 = state_33748;
state_33748 = G__33866;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = function(state_33748){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1.call(this,state_33748);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___33858,jobs,results,process__$1,async))
})();
var state__33497__auto__ = (function (){var statearr_33763 = f__33496__auto__.call(null);
(statearr_33763[(6)] = c__33495__auto___33858);

return statearr_33763;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___33858,jobs,results,process__$1,async))
);


var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__,jobs,results,process__$1,async){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__,jobs,results,process__$1,async){
return (function (state_33801){
var state_val_33802 = (state_33801[(1)]);
if((state_val_33802 === (7))){
var inst_33797 = (state_33801[(2)]);
var state_33801__$1 = state_33801;
var statearr_33803_33867 = state_33801__$1;
(statearr_33803_33867[(2)] = inst_33797);

(statearr_33803_33867[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (20))){
var state_33801__$1 = state_33801;
var statearr_33804_33868 = state_33801__$1;
(statearr_33804_33868[(2)] = null);

(statearr_33804_33868[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (1))){
var state_33801__$1 = state_33801;
var statearr_33805_33869 = state_33801__$1;
(statearr_33805_33869[(2)] = null);

(statearr_33805_33869[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (4))){
var inst_33766 = (state_33801[(7)]);
var inst_33766__$1 = (state_33801[(2)]);
var inst_33767 = (inst_33766__$1 == null);
var state_33801__$1 = (function (){var statearr_33806 = state_33801;
(statearr_33806[(7)] = inst_33766__$1);

return statearr_33806;
})();
if(cljs.core.truth_(inst_33767)){
var statearr_33807_33870 = state_33801__$1;
(statearr_33807_33870[(1)] = (5));

} else {
var statearr_33808_33871 = state_33801__$1;
(statearr_33808_33871[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (15))){
var inst_33779 = (state_33801[(8)]);
var state_33801__$1 = state_33801;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_33801__$1,(18),to,inst_33779);
} else {
if((state_val_33802 === (21))){
var inst_33792 = (state_33801[(2)]);
var state_33801__$1 = state_33801;
var statearr_33809_33872 = state_33801__$1;
(statearr_33809_33872[(2)] = inst_33792);

(statearr_33809_33872[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (13))){
var inst_33794 = (state_33801[(2)]);
var state_33801__$1 = (function (){var statearr_33810 = state_33801;
(statearr_33810[(9)] = inst_33794);

return statearr_33810;
})();
var statearr_33811_33873 = state_33801__$1;
(statearr_33811_33873[(2)] = null);

(statearr_33811_33873[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (6))){
var inst_33766 = (state_33801[(7)]);
var state_33801__$1 = state_33801;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33801__$1,(11),inst_33766);
} else {
if((state_val_33802 === (17))){
var inst_33787 = (state_33801[(2)]);
var state_33801__$1 = state_33801;
if(cljs.core.truth_(inst_33787)){
var statearr_33812_33874 = state_33801__$1;
(statearr_33812_33874[(1)] = (19));

} else {
var statearr_33813_33875 = state_33801__$1;
(statearr_33813_33875[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (3))){
var inst_33799 = (state_33801[(2)]);
var state_33801__$1 = state_33801;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33801__$1,inst_33799);
} else {
if((state_val_33802 === (12))){
var inst_33776 = (state_33801[(10)]);
var state_33801__$1 = state_33801;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33801__$1,(14),inst_33776);
} else {
if((state_val_33802 === (2))){
var state_33801__$1 = state_33801;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33801__$1,(4),results);
} else {
if((state_val_33802 === (19))){
var state_33801__$1 = state_33801;
var statearr_33814_33876 = state_33801__$1;
(statearr_33814_33876[(2)] = null);

(statearr_33814_33876[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (11))){
var inst_33776 = (state_33801[(2)]);
var state_33801__$1 = (function (){var statearr_33815 = state_33801;
(statearr_33815[(10)] = inst_33776);

return statearr_33815;
})();
var statearr_33816_33877 = state_33801__$1;
(statearr_33816_33877[(2)] = null);

(statearr_33816_33877[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (9))){
var state_33801__$1 = state_33801;
var statearr_33817_33878 = state_33801__$1;
(statearr_33817_33878[(2)] = null);

(statearr_33817_33878[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (5))){
var state_33801__$1 = state_33801;
if(cljs.core.truth_(close_QMARK_)){
var statearr_33818_33879 = state_33801__$1;
(statearr_33818_33879[(1)] = (8));

} else {
var statearr_33819_33880 = state_33801__$1;
(statearr_33819_33880[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (14))){
var inst_33779 = (state_33801[(8)]);
var inst_33781 = (state_33801[(11)]);
var inst_33779__$1 = (state_33801[(2)]);
var inst_33780 = (inst_33779__$1 == null);
var inst_33781__$1 = cljs.core.not.call(null,inst_33780);
var state_33801__$1 = (function (){var statearr_33820 = state_33801;
(statearr_33820[(8)] = inst_33779__$1);

(statearr_33820[(11)] = inst_33781__$1);

return statearr_33820;
})();
if(inst_33781__$1){
var statearr_33821_33881 = state_33801__$1;
(statearr_33821_33881[(1)] = (15));

} else {
var statearr_33822_33882 = state_33801__$1;
(statearr_33822_33882[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (16))){
var inst_33781 = (state_33801[(11)]);
var state_33801__$1 = state_33801;
var statearr_33823_33883 = state_33801__$1;
(statearr_33823_33883[(2)] = inst_33781);

(statearr_33823_33883[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (10))){
var inst_33773 = (state_33801[(2)]);
var state_33801__$1 = state_33801;
var statearr_33824_33884 = state_33801__$1;
(statearr_33824_33884[(2)] = inst_33773);

(statearr_33824_33884[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (18))){
var inst_33784 = (state_33801[(2)]);
var state_33801__$1 = state_33801;
var statearr_33825_33885 = state_33801__$1;
(statearr_33825_33885[(2)] = inst_33784);

(statearr_33825_33885[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33802 === (8))){
var inst_33770 = cljs.core.async.close_BANG_.call(null,to);
var state_33801__$1 = state_33801;
var statearr_33826_33886 = state_33801__$1;
(statearr_33826_33886[(2)] = inst_33770);

(statearr_33826_33886[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto__,jobs,results,process__$1,async))
;
return ((function (switch__33350__auto__,c__33495__auto__,jobs,results,process__$1,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0 = (function (){
var statearr_33827 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_33827[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__);

(statearr_33827[(1)] = (1));

return statearr_33827;
});
var cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1 = (function (state_33801){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33801);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33828){if((e33828 instanceof Object)){
var ex__33354__auto__ = e33828;
var statearr_33829_33887 = state_33801;
(statearr_33829_33887[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33801);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33828;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__33888 = state_33801;
state_33801 = G__33888;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__ = function(state_33801){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1.call(this,state_33801);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__33351__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__,jobs,results,process__$1,async))
})();
var state__33497__auto__ = (function (){var statearr_33830 = f__33496__auto__.call(null);
(statearr_33830[(6)] = c__33495__auto__);

return statearr_33830;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__,jobs,results,process__$1,async))
);

return c__33495__auto__;
});
/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the async function af, with parallelism n. af
 *   must be a function of two arguments, the first an input value and
 *   the second a channel on which to place the result(s). af must close!
 *   the channel before returning.  The presumption is that af will
 *   return immediately, having launched some asynchronous operation
 *   whose completion/callback will manipulate the result channel. Outputs
 *   will be returned in order relative to  the inputs. By default, the to
 *   channel will be closed when the from channel closes, but can be
 *   determined by the close?  parameter. Will stop consuming the from
 *   channel if the to channel closes.
 */
cljs.core.async.pipeline_async = (function cljs$core$async$pipeline_async(var_args){
var G__33890 = arguments.length;
switch (G__33890) {
case 4:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4 = (function (n,to,af,from){
return cljs.core.async.pipeline_async.call(null,n,to,af,from,true);
});

cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5 = (function (n,to,af,from,close_QMARK_){
return cljs.core.async.pipeline_STAR_.call(null,n,to,af,from,close_QMARK_,null,new cljs.core.Keyword(null,"async","async",1050769601));
});

cljs.core.async.pipeline_async.cljs$lang$maxFixedArity = 5;

/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the transducer xf, with parallelism n. Because
 *   it is parallel, the transducer will be applied independently to each
 *   element, not across elements, and may produce zero or more outputs
 *   per input.  Outputs will be returned in order relative to the
 *   inputs. By default, the to channel will be closed when the from
 *   channel closes, but can be determined by the close?  parameter. Will
 *   stop consuming the from channel if the to channel closes.
 * 
 *   Note this is supplied for API compatibility with the Clojure version.
 *   Values of N > 1 will not result in actual concurrency in a
 *   single-threaded runtime.
 */
cljs.core.async.pipeline = (function cljs$core$async$pipeline(var_args){
var G__33893 = arguments.length;
switch (G__33893) {
case 4:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
case 6:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4 = (function (n,to,xf,from){
return cljs.core.async.pipeline.call(null,n,to,xf,from,true);
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5 = (function (n,to,xf,from,close_QMARK_){
return cljs.core.async.pipeline.call(null,n,to,xf,from,close_QMARK_,null);
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6 = (function (n,to,xf,from,close_QMARK_,ex_handler){
return cljs.core.async.pipeline_STAR_.call(null,n,to,xf,from,close_QMARK_,ex_handler,new cljs.core.Keyword(null,"compute","compute",1555393130));
});

cljs.core.async.pipeline.cljs$lang$maxFixedArity = 6;

/**
 * Takes a predicate and a source channel and returns a vector of two
 *   channels, the first of which will contain the values for which the
 *   predicate returned true, the second those for which it returned
 *   false.
 * 
 *   The out channels will be unbuffered by default, or two buf-or-ns can
 *   be supplied. The channels will close after the source channel has
 *   closed.
 */
cljs.core.async.split = (function cljs$core$async$split(var_args){
var G__33896 = arguments.length;
switch (G__33896) {
case 2:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.split.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.split.call(null,p,ch,null,null);
});

cljs.core.async.split.cljs$core$IFn$_invoke$arity$4 = (function (p,ch,t_buf_or_n,f_buf_or_n){
var tc = cljs.core.async.chan.call(null,t_buf_or_n);
var fc = cljs.core.async.chan.call(null,f_buf_or_n);
var c__33495__auto___33945 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___33945,tc,fc){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___33945,tc,fc){
return (function (state_33922){
var state_val_33923 = (state_33922[(1)]);
if((state_val_33923 === (7))){
var inst_33918 = (state_33922[(2)]);
var state_33922__$1 = state_33922;
var statearr_33924_33946 = state_33922__$1;
(statearr_33924_33946[(2)] = inst_33918);

(statearr_33924_33946[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (1))){
var state_33922__$1 = state_33922;
var statearr_33925_33947 = state_33922__$1;
(statearr_33925_33947[(2)] = null);

(statearr_33925_33947[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (4))){
var inst_33899 = (state_33922[(7)]);
var inst_33899__$1 = (state_33922[(2)]);
var inst_33900 = (inst_33899__$1 == null);
var state_33922__$1 = (function (){var statearr_33926 = state_33922;
(statearr_33926[(7)] = inst_33899__$1);

return statearr_33926;
})();
if(cljs.core.truth_(inst_33900)){
var statearr_33927_33948 = state_33922__$1;
(statearr_33927_33948[(1)] = (5));

} else {
var statearr_33928_33949 = state_33922__$1;
(statearr_33928_33949[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (13))){
var state_33922__$1 = state_33922;
var statearr_33929_33950 = state_33922__$1;
(statearr_33929_33950[(2)] = null);

(statearr_33929_33950[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (6))){
var inst_33899 = (state_33922[(7)]);
var inst_33905 = p.call(null,inst_33899);
var state_33922__$1 = state_33922;
if(cljs.core.truth_(inst_33905)){
var statearr_33930_33951 = state_33922__$1;
(statearr_33930_33951[(1)] = (9));

} else {
var statearr_33931_33952 = state_33922__$1;
(statearr_33931_33952[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (3))){
var inst_33920 = (state_33922[(2)]);
var state_33922__$1 = state_33922;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33922__$1,inst_33920);
} else {
if((state_val_33923 === (12))){
var state_33922__$1 = state_33922;
var statearr_33932_33953 = state_33922__$1;
(statearr_33932_33953[(2)] = null);

(statearr_33932_33953[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (2))){
var state_33922__$1 = state_33922;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33922__$1,(4),ch);
} else {
if((state_val_33923 === (11))){
var inst_33899 = (state_33922[(7)]);
var inst_33909 = (state_33922[(2)]);
var state_33922__$1 = state_33922;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_33922__$1,(8),inst_33909,inst_33899);
} else {
if((state_val_33923 === (9))){
var state_33922__$1 = state_33922;
var statearr_33933_33954 = state_33922__$1;
(statearr_33933_33954[(2)] = tc);

(statearr_33933_33954[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (5))){
var inst_33902 = cljs.core.async.close_BANG_.call(null,tc);
var inst_33903 = cljs.core.async.close_BANG_.call(null,fc);
var state_33922__$1 = (function (){var statearr_33934 = state_33922;
(statearr_33934[(8)] = inst_33902);

return statearr_33934;
})();
var statearr_33935_33955 = state_33922__$1;
(statearr_33935_33955[(2)] = inst_33903);

(statearr_33935_33955[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (14))){
var inst_33916 = (state_33922[(2)]);
var state_33922__$1 = state_33922;
var statearr_33936_33956 = state_33922__$1;
(statearr_33936_33956[(2)] = inst_33916);

(statearr_33936_33956[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (10))){
var state_33922__$1 = state_33922;
var statearr_33937_33957 = state_33922__$1;
(statearr_33937_33957[(2)] = fc);

(statearr_33937_33957[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33923 === (8))){
var inst_33911 = (state_33922[(2)]);
var state_33922__$1 = state_33922;
if(cljs.core.truth_(inst_33911)){
var statearr_33938_33958 = state_33922__$1;
(statearr_33938_33958[(1)] = (12));

} else {
var statearr_33939_33959 = state_33922__$1;
(statearr_33939_33959[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___33945,tc,fc))
;
return ((function (switch__33350__auto__,c__33495__auto___33945,tc,fc){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_33940 = [null,null,null,null,null,null,null,null,null];
(statearr_33940[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_33940[(1)] = (1));

return statearr_33940;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_33922){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33922);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33941){if((e33941 instanceof Object)){
var ex__33354__auto__ = e33941;
var statearr_33942_33960 = state_33922;
(statearr_33942_33960[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33922);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33941;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__33961 = state_33922;
state_33922 = G__33961;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_33922){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_33922);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___33945,tc,fc))
})();
var state__33497__auto__ = (function (){var statearr_33943 = f__33496__auto__.call(null);
(statearr_33943[(6)] = c__33495__auto___33945);

return statearr_33943;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___33945,tc,fc))
);


return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [tc,fc], null);
});

cljs.core.async.split.cljs$lang$maxFixedArity = 4;

/**
 * f should be a function of 2 arguments. Returns a channel containing
 *   the single result of applying f to init and the first item from the
 *   channel, then applying f to that result and the 2nd item, etc. If
 *   the channel closes without yielding items, returns init and f is not
 *   called. ch must close before reduce produces a result.
 */
cljs.core.async.reduce = (function cljs$core$async$reduce(f,init,ch){
var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__){
return (function (state_33982){
var state_val_33983 = (state_33982[(1)]);
if((state_val_33983 === (7))){
var inst_33978 = (state_33982[(2)]);
var state_33982__$1 = state_33982;
var statearr_33984_34002 = state_33982__$1;
(statearr_33984_34002[(2)] = inst_33978);

(statearr_33984_34002[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33983 === (1))){
var inst_33962 = init;
var state_33982__$1 = (function (){var statearr_33985 = state_33982;
(statearr_33985[(7)] = inst_33962);

return statearr_33985;
})();
var statearr_33986_34003 = state_33982__$1;
(statearr_33986_34003[(2)] = null);

(statearr_33986_34003[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33983 === (4))){
var inst_33965 = (state_33982[(8)]);
var inst_33965__$1 = (state_33982[(2)]);
var inst_33966 = (inst_33965__$1 == null);
var state_33982__$1 = (function (){var statearr_33987 = state_33982;
(statearr_33987[(8)] = inst_33965__$1);

return statearr_33987;
})();
if(cljs.core.truth_(inst_33966)){
var statearr_33988_34004 = state_33982__$1;
(statearr_33988_34004[(1)] = (5));

} else {
var statearr_33989_34005 = state_33982__$1;
(statearr_33989_34005[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33983 === (6))){
var inst_33965 = (state_33982[(8)]);
var inst_33962 = (state_33982[(7)]);
var inst_33969 = (state_33982[(9)]);
var inst_33969__$1 = f.call(null,inst_33962,inst_33965);
var inst_33970 = cljs.core.reduced_QMARK_.call(null,inst_33969__$1);
var state_33982__$1 = (function (){var statearr_33990 = state_33982;
(statearr_33990[(9)] = inst_33969__$1);

return statearr_33990;
})();
if(inst_33970){
var statearr_33991_34006 = state_33982__$1;
(statearr_33991_34006[(1)] = (8));

} else {
var statearr_33992_34007 = state_33982__$1;
(statearr_33992_34007[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33983 === (3))){
var inst_33980 = (state_33982[(2)]);
var state_33982__$1 = state_33982;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_33982__$1,inst_33980);
} else {
if((state_val_33983 === (2))){
var state_33982__$1 = state_33982;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_33982__$1,(4),ch);
} else {
if((state_val_33983 === (9))){
var inst_33969 = (state_33982[(9)]);
var inst_33962 = inst_33969;
var state_33982__$1 = (function (){var statearr_33993 = state_33982;
(statearr_33993[(7)] = inst_33962);

return statearr_33993;
})();
var statearr_33994_34008 = state_33982__$1;
(statearr_33994_34008[(2)] = null);

(statearr_33994_34008[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33983 === (5))){
var inst_33962 = (state_33982[(7)]);
var state_33982__$1 = state_33982;
var statearr_33995_34009 = state_33982__$1;
(statearr_33995_34009[(2)] = inst_33962);

(statearr_33995_34009[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33983 === (10))){
var inst_33976 = (state_33982[(2)]);
var state_33982__$1 = state_33982;
var statearr_33996_34010 = state_33982__$1;
(statearr_33996_34010[(2)] = inst_33976);

(statearr_33996_34010[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_33983 === (8))){
var inst_33969 = (state_33982[(9)]);
var inst_33972 = cljs.core.deref.call(null,inst_33969);
var state_33982__$1 = state_33982;
var statearr_33997_34011 = state_33982__$1;
(statearr_33997_34011[(2)] = inst_33972);

(statearr_33997_34011[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto__))
;
return ((function (switch__33350__auto__,c__33495__auto__){
return (function() {
var cljs$core$async$reduce_$_state_machine__33351__auto__ = null;
var cljs$core$async$reduce_$_state_machine__33351__auto____0 = (function (){
var statearr_33998 = [null,null,null,null,null,null,null,null,null,null];
(statearr_33998[(0)] = cljs$core$async$reduce_$_state_machine__33351__auto__);

(statearr_33998[(1)] = (1));

return statearr_33998;
});
var cljs$core$async$reduce_$_state_machine__33351__auto____1 = (function (state_33982){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_33982);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e33999){if((e33999 instanceof Object)){
var ex__33354__auto__ = e33999;
var statearr_34000_34012 = state_33982;
(statearr_34000_34012[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_33982);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e33999;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34013 = state_33982;
state_33982 = G__34013;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$reduce_$_state_machine__33351__auto__ = function(state_33982){
switch(arguments.length){
case 0:
return cljs$core$async$reduce_$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$reduce_$_state_machine__33351__auto____1.call(this,state_33982);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$reduce_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$reduce_$_state_machine__33351__auto____0;
cljs$core$async$reduce_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$reduce_$_state_machine__33351__auto____1;
return cljs$core$async$reduce_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__))
})();
var state__33497__auto__ = (function (){var statearr_34001 = f__33496__auto__.call(null);
(statearr_34001[(6)] = c__33495__auto__);

return statearr_34001;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__))
);

return c__33495__auto__;
});
/**
 * async/reduces a channel with a transformation (xform f).
 *   Returns a channel containing the result.  ch must close before
 *   transduce produces a result.
 */
cljs.core.async.transduce = (function cljs$core$async$transduce(xform,f,init,ch){
var f__$1 = xform.call(null,f);
var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__,f__$1){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__,f__$1){
return (function (state_34019){
var state_val_34020 = (state_34019[(1)]);
if((state_val_34020 === (1))){
var inst_34014 = cljs.core.async.reduce.call(null,f__$1,init,ch);
var state_34019__$1 = state_34019;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_34019__$1,(2),inst_34014);
} else {
if((state_val_34020 === (2))){
var inst_34016 = (state_34019[(2)]);
var inst_34017 = f__$1.call(null,inst_34016);
var state_34019__$1 = state_34019;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34019__$1,inst_34017);
} else {
return null;
}
}
});})(c__33495__auto__,f__$1))
;
return ((function (switch__33350__auto__,c__33495__auto__,f__$1){
return (function() {
var cljs$core$async$transduce_$_state_machine__33351__auto__ = null;
var cljs$core$async$transduce_$_state_machine__33351__auto____0 = (function (){
var statearr_34021 = [null,null,null,null,null,null,null];
(statearr_34021[(0)] = cljs$core$async$transduce_$_state_machine__33351__auto__);

(statearr_34021[(1)] = (1));

return statearr_34021;
});
var cljs$core$async$transduce_$_state_machine__33351__auto____1 = (function (state_34019){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34019);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34022){if((e34022 instanceof Object)){
var ex__33354__auto__ = e34022;
var statearr_34023_34025 = state_34019;
(statearr_34023_34025[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34019);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34022;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34026 = state_34019;
state_34019 = G__34026;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$transduce_$_state_machine__33351__auto__ = function(state_34019){
switch(arguments.length){
case 0:
return cljs$core$async$transduce_$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$transduce_$_state_machine__33351__auto____1.call(this,state_34019);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$transduce_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$transduce_$_state_machine__33351__auto____0;
cljs$core$async$transduce_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$transduce_$_state_machine__33351__auto____1;
return cljs$core$async$transduce_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__,f__$1))
})();
var state__33497__auto__ = (function (){var statearr_34024 = f__33496__auto__.call(null);
(statearr_34024[(6)] = c__33495__auto__);

return statearr_34024;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__,f__$1))
);

return c__33495__auto__;
});
/**
 * Puts the contents of coll into the supplied channel.
 * 
 *   By default the channel will be closed after the items are copied,
 *   but can be determined by the close? parameter.
 * 
 *   Returns a channel which will close after the items are copied.
 */
cljs.core.async.onto_chan = (function cljs$core$async$onto_chan(var_args){
var G__34028 = arguments.length;
switch (G__34028) {
case 2:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2 = (function (ch,coll){
return cljs.core.async.onto_chan.call(null,ch,coll,true);
});

cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3 = (function (ch,coll,close_QMARK_){
var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__){
return (function (state_34053){
var state_val_34054 = (state_34053[(1)]);
if((state_val_34054 === (7))){
var inst_34035 = (state_34053[(2)]);
var state_34053__$1 = state_34053;
var statearr_34055_34076 = state_34053__$1;
(statearr_34055_34076[(2)] = inst_34035);

(statearr_34055_34076[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (1))){
var inst_34029 = cljs.core.seq.call(null,coll);
var inst_34030 = inst_34029;
var state_34053__$1 = (function (){var statearr_34056 = state_34053;
(statearr_34056[(7)] = inst_34030);

return statearr_34056;
})();
var statearr_34057_34077 = state_34053__$1;
(statearr_34057_34077[(2)] = null);

(statearr_34057_34077[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (4))){
var inst_34030 = (state_34053[(7)]);
var inst_34033 = cljs.core.first.call(null,inst_34030);
var state_34053__$1 = state_34053;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_34053__$1,(7),ch,inst_34033);
} else {
if((state_val_34054 === (13))){
var inst_34047 = (state_34053[(2)]);
var state_34053__$1 = state_34053;
var statearr_34058_34078 = state_34053__$1;
(statearr_34058_34078[(2)] = inst_34047);

(statearr_34058_34078[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (6))){
var inst_34038 = (state_34053[(2)]);
var state_34053__$1 = state_34053;
if(cljs.core.truth_(inst_34038)){
var statearr_34059_34079 = state_34053__$1;
(statearr_34059_34079[(1)] = (8));

} else {
var statearr_34060_34080 = state_34053__$1;
(statearr_34060_34080[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (3))){
var inst_34051 = (state_34053[(2)]);
var state_34053__$1 = state_34053;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34053__$1,inst_34051);
} else {
if((state_val_34054 === (12))){
var state_34053__$1 = state_34053;
var statearr_34061_34081 = state_34053__$1;
(statearr_34061_34081[(2)] = null);

(statearr_34061_34081[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (2))){
var inst_34030 = (state_34053[(7)]);
var state_34053__$1 = state_34053;
if(cljs.core.truth_(inst_34030)){
var statearr_34062_34082 = state_34053__$1;
(statearr_34062_34082[(1)] = (4));

} else {
var statearr_34063_34083 = state_34053__$1;
(statearr_34063_34083[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (11))){
var inst_34044 = cljs.core.async.close_BANG_.call(null,ch);
var state_34053__$1 = state_34053;
var statearr_34064_34084 = state_34053__$1;
(statearr_34064_34084[(2)] = inst_34044);

(statearr_34064_34084[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (9))){
var state_34053__$1 = state_34053;
if(cljs.core.truth_(close_QMARK_)){
var statearr_34065_34085 = state_34053__$1;
(statearr_34065_34085[(1)] = (11));

} else {
var statearr_34066_34086 = state_34053__$1;
(statearr_34066_34086[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (5))){
var inst_34030 = (state_34053[(7)]);
var state_34053__$1 = state_34053;
var statearr_34067_34087 = state_34053__$1;
(statearr_34067_34087[(2)] = inst_34030);

(statearr_34067_34087[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (10))){
var inst_34049 = (state_34053[(2)]);
var state_34053__$1 = state_34053;
var statearr_34068_34088 = state_34053__$1;
(statearr_34068_34088[(2)] = inst_34049);

(statearr_34068_34088[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34054 === (8))){
var inst_34030 = (state_34053[(7)]);
var inst_34040 = cljs.core.next.call(null,inst_34030);
var inst_34030__$1 = inst_34040;
var state_34053__$1 = (function (){var statearr_34069 = state_34053;
(statearr_34069[(7)] = inst_34030__$1);

return statearr_34069;
})();
var statearr_34070_34089 = state_34053__$1;
(statearr_34070_34089[(2)] = null);

(statearr_34070_34089[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto__))
;
return ((function (switch__33350__auto__,c__33495__auto__){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_34071 = [null,null,null,null,null,null,null,null];
(statearr_34071[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_34071[(1)] = (1));

return statearr_34071;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_34053){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34053);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34072){if((e34072 instanceof Object)){
var ex__33354__auto__ = e34072;
var statearr_34073_34090 = state_34053;
(statearr_34073_34090[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34053);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34072;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34091 = state_34053;
state_34053 = G__34091;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_34053){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_34053);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__))
})();
var state__33497__auto__ = (function (){var statearr_34074 = f__33496__auto__.call(null);
(statearr_34074[(6)] = c__33495__auto__);

return statearr_34074;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__))
);

return c__33495__auto__;
});

cljs.core.async.onto_chan.cljs$lang$maxFixedArity = 3;

/**
 * Creates and returns a channel which contains the contents of coll,
 *   closing when exhausted.
 */
cljs.core.async.to_chan = (function cljs$core$async$to_chan(coll){
var ch = cljs.core.async.chan.call(null,cljs.core.bounded_count.call(null,(100),coll));
cljs.core.async.onto_chan.call(null,ch,coll);

return ch;
});

/**
 * @interface
 */
cljs.core.async.Mux = function(){};

cljs.core.async.muxch_STAR_ = (function cljs$core$async$muxch_STAR_(_){
if((((!((_ == null)))) && ((!((_.cljs$core$async$Mux$muxch_STAR_$arity$1 == null)))))){
return _.cljs$core$async$Mux$muxch_STAR_$arity$1(_);
} else {
var x__4433__auto__ = (((_ == null))?null:_);
var m__4434__auto__ = (cljs.core.async.muxch_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,_);
} else {
var m__4431__auto__ = (cljs.core.async.muxch_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,_);
} else {
throw cljs.core.missing_protocol.call(null,"Mux.muxch*",_);
}
}
}
});


/**
 * @interface
 */
cljs.core.async.Mult = function(){};

cljs.core.async.tap_STAR_ = (function cljs$core$async$tap_STAR_(m,ch,close_QMARK_){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$tap_STAR_$arity$3 == null)))))){
return m.cljs$core$async$Mult$tap_STAR_$arity$3(m,ch,close_QMARK_);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.tap_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch,close_QMARK_);
} else {
var m__4431__auto__ = (cljs.core.async.tap_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch,close_QMARK_);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.tap*",m);
}
}
}
});

cljs.core.async.untap_STAR_ = (function cljs$core$async$untap_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mult$untap_STAR_$arity$2(m,ch);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.untap_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch);
} else {
var m__4431__auto__ = (cljs.core.async.untap_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.untap*",m);
}
}
}
});

cljs.core.async.untap_all_STAR_ = (function cljs$core$async$untap_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mult$untap_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mult$untap_all_STAR_$arity$1(m);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.untap_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m);
} else {
var m__4431__auto__ = (cljs.core.async.untap_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.untap-all*",m);
}
}
}
});

/**
 * Creates and returns a mult(iple) of the supplied channel. Channels
 *   containing copies of the channel can be created with 'tap', and
 *   detached with 'untap'.
 * 
 *   Each item is distributed to all taps in parallel and synchronously,
 *   i.e. each tap must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow taps from holding up the mult.
 * 
 *   Items received when there are no taps get dropped.
 * 
 *   If a tap puts to a closed channel, it will be removed from the mult.
 */
cljs.core.async.mult = (function cljs$core$async$mult(ch){
var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var m = (function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async34092 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.Mult}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34092 = (function (ch,cs,meta34093){
this.ch = ch;
this.cs = cs;
this.meta34093 = meta34093;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs){
return (function (_34094,meta34093__$1){
var self__ = this;
var _34094__$1 = this;
return (new cljs.core.async.t_cljs$core$async34092(self__.ch,self__.cs,meta34093__$1));
});})(cs))
;

cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs){
return (function (_34094){
var self__ = this;
var _34094__$1 = this;
return self__.meta34093;
});})(cs))
;

cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
});})(cs))
;

cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$async$Mult$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = ((function (cs){
return (function (_,ch__$1,close_QMARK_){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch__$1,close_QMARK_);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = ((function (cs){
return (function (_,ch__$1){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch__$1);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async34092.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = ((function (cs){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async34092.getBasis = ((function (cs){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"meta34093","meta34093",18576769,null)], null);
});})(cs))
;

cljs.core.async.t_cljs$core$async34092.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async34092.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34092";

cljs.core.async.t_cljs$core$async34092.cljs$lang$ctorPrWriter = ((function (cs){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async34092");
});})(cs))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34092.
 */
cljs.core.async.__GT_t_cljs$core$async34092 = ((function (cs){
return (function cljs$core$async$mult_$___GT_t_cljs$core$async34092(ch__$1,cs__$1,meta34093){
return (new cljs.core.async.t_cljs$core$async34092(ch__$1,cs__$1,meta34093));
});})(cs))
;

}

return (new cljs.core.async.t_cljs$core$async34092(ch,cs,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var dchan = cljs.core.async.chan.call(null,(1));
var dctr = cljs.core.atom.call(null,null);
var done = ((function (cs,m,dchan,dctr){
return (function (_){
if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.call(null,dchan,true);
} else {
return null;
}
});})(cs,m,dchan,dctr))
;
var c__33495__auto___34314 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___34314,cs,m,dchan,dctr,done){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___34314,cs,m,dchan,dctr,done){
return (function (state_34229){
var state_val_34230 = (state_34229[(1)]);
if((state_val_34230 === (7))){
var inst_34225 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34231_34315 = state_34229__$1;
(statearr_34231_34315[(2)] = inst_34225);

(statearr_34231_34315[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (20))){
var inst_34128 = (state_34229[(7)]);
var inst_34140 = cljs.core.first.call(null,inst_34128);
var inst_34141 = cljs.core.nth.call(null,inst_34140,(0),null);
var inst_34142 = cljs.core.nth.call(null,inst_34140,(1),null);
var state_34229__$1 = (function (){var statearr_34232 = state_34229;
(statearr_34232[(8)] = inst_34141);

return statearr_34232;
})();
if(cljs.core.truth_(inst_34142)){
var statearr_34233_34316 = state_34229__$1;
(statearr_34233_34316[(1)] = (22));

} else {
var statearr_34234_34317 = state_34229__$1;
(statearr_34234_34317[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (27))){
var inst_34170 = (state_34229[(9)]);
var inst_34097 = (state_34229[(10)]);
var inst_34172 = (state_34229[(11)]);
var inst_34177 = (state_34229[(12)]);
var inst_34177__$1 = cljs.core._nth.call(null,inst_34170,inst_34172);
var inst_34178 = cljs.core.async.put_BANG_.call(null,inst_34177__$1,inst_34097,done);
var state_34229__$1 = (function (){var statearr_34235 = state_34229;
(statearr_34235[(12)] = inst_34177__$1);

return statearr_34235;
})();
if(cljs.core.truth_(inst_34178)){
var statearr_34236_34318 = state_34229__$1;
(statearr_34236_34318[(1)] = (30));

} else {
var statearr_34237_34319 = state_34229__$1;
(statearr_34237_34319[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (1))){
var state_34229__$1 = state_34229;
var statearr_34238_34320 = state_34229__$1;
(statearr_34238_34320[(2)] = null);

(statearr_34238_34320[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (24))){
var inst_34128 = (state_34229[(7)]);
var inst_34147 = (state_34229[(2)]);
var inst_34148 = cljs.core.next.call(null,inst_34128);
var inst_34106 = inst_34148;
var inst_34107 = null;
var inst_34108 = (0);
var inst_34109 = (0);
var state_34229__$1 = (function (){var statearr_34239 = state_34229;
(statearr_34239[(13)] = inst_34107);

(statearr_34239[(14)] = inst_34147);

(statearr_34239[(15)] = inst_34109);

(statearr_34239[(16)] = inst_34106);

(statearr_34239[(17)] = inst_34108);

return statearr_34239;
})();
var statearr_34240_34321 = state_34229__$1;
(statearr_34240_34321[(2)] = null);

(statearr_34240_34321[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (39))){
var state_34229__$1 = state_34229;
var statearr_34244_34322 = state_34229__$1;
(statearr_34244_34322[(2)] = null);

(statearr_34244_34322[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (4))){
var inst_34097 = (state_34229[(10)]);
var inst_34097__$1 = (state_34229[(2)]);
var inst_34098 = (inst_34097__$1 == null);
var state_34229__$1 = (function (){var statearr_34245 = state_34229;
(statearr_34245[(10)] = inst_34097__$1);

return statearr_34245;
})();
if(cljs.core.truth_(inst_34098)){
var statearr_34246_34323 = state_34229__$1;
(statearr_34246_34323[(1)] = (5));

} else {
var statearr_34247_34324 = state_34229__$1;
(statearr_34247_34324[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (15))){
var inst_34107 = (state_34229[(13)]);
var inst_34109 = (state_34229[(15)]);
var inst_34106 = (state_34229[(16)]);
var inst_34108 = (state_34229[(17)]);
var inst_34124 = (state_34229[(2)]);
var inst_34125 = (inst_34109 + (1));
var tmp34241 = inst_34107;
var tmp34242 = inst_34106;
var tmp34243 = inst_34108;
var inst_34106__$1 = tmp34242;
var inst_34107__$1 = tmp34241;
var inst_34108__$1 = tmp34243;
var inst_34109__$1 = inst_34125;
var state_34229__$1 = (function (){var statearr_34248 = state_34229;
(statearr_34248[(13)] = inst_34107__$1);

(statearr_34248[(15)] = inst_34109__$1);

(statearr_34248[(16)] = inst_34106__$1);

(statearr_34248[(17)] = inst_34108__$1);

(statearr_34248[(18)] = inst_34124);

return statearr_34248;
})();
var statearr_34249_34325 = state_34229__$1;
(statearr_34249_34325[(2)] = null);

(statearr_34249_34325[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (21))){
var inst_34151 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34253_34326 = state_34229__$1;
(statearr_34253_34326[(2)] = inst_34151);

(statearr_34253_34326[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (31))){
var inst_34177 = (state_34229[(12)]);
var inst_34181 = done.call(null,null);
var inst_34182 = cljs.core.async.untap_STAR_.call(null,m,inst_34177);
var state_34229__$1 = (function (){var statearr_34254 = state_34229;
(statearr_34254[(19)] = inst_34181);

return statearr_34254;
})();
var statearr_34255_34327 = state_34229__$1;
(statearr_34255_34327[(2)] = inst_34182);

(statearr_34255_34327[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (32))){
var inst_34170 = (state_34229[(9)]);
var inst_34171 = (state_34229[(20)]);
var inst_34169 = (state_34229[(21)]);
var inst_34172 = (state_34229[(11)]);
var inst_34184 = (state_34229[(2)]);
var inst_34185 = (inst_34172 + (1));
var tmp34250 = inst_34170;
var tmp34251 = inst_34171;
var tmp34252 = inst_34169;
var inst_34169__$1 = tmp34252;
var inst_34170__$1 = tmp34250;
var inst_34171__$1 = tmp34251;
var inst_34172__$1 = inst_34185;
var state_34229__$1 = (function (){var statearr_34256 = state_34229;
(statearr_34256[(9)] = inst_34170__$1);

(statearr_34256[(20)] = inst_34171__$1);

(statearr_34256[(21)] = inst_34169__$1);

(statearr_34256[(11)] = inst_34172__$1);

(statearr_34256[(22)] = inst_34184);

return statearr_34256;
})();
var statearr_34257_34328 = state_34229__$1;
(statearr_34257_34328[(2)] = null);

(statearr_34257_34328[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (40))){
var inst_34197 = (state_34229[(23)]);
var inst_34201 = done.call(null,null);
var inst_34202 = cljs.core.async.untap_STAR_.call(null,m,inst_34197);
var state_34229__$1 = (function (){var statearr_34258 = state_34229;
(statearr_34258[(24)] = inst_34201);

return statearr_34258;
})();
var statearr_34259_34329 = state_34229__$1;
(statearr_34259_34329[(2)] = inst_34202);

(statearr_34259_34329[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (33))){
var inst_34188 = (state_34229[(25)]);
var inst_34190 = cljs.core.chunked_seq_QMARK_.call(null,inst_34188);
var state_34229__$1 = state_34229;
if(inst_34190){
var statearr_34260_34330 = state_34229__$1;
(statearr_34260_34330[(1)] = (36));

} else {
var statearr_34261_34331 = state_34229__$1;
(statearr_34261_34331[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (13))){
var inst_34118 = (state_34229[(26)]);
var inst_34121 = cljs.core.async.close_BANG_.call(null,inst_34118);
var state_34229__$1 = state_34229;
var statearr_34262_34332 = state_34229__$1;
(statearr_34262_34332[(2)] = inst_34121);

(statearr_34262_34332[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (22))){
var inst_34141 = (state_34229[(8)]);
var inst_34144 = cljs.core.async.close_BANG_.call(null,inst_34141);
var state_34229__$1 = state_34229;
var statearr_34263_34333 = state_34229__$1;
(statearr_34263_34333[(2)] = inst_34144);

(statearr_34263_34333[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (36))){
var inst_34188 = (state_34229[(25)]);
var inst_34192 = cljs.core.chunk_first.call(null,inst_34188);
var inst_34193 = cljs.core.chunk_rest.call(null,inst_34188);
var inst_34194 = cljs.core.count.call(null,inst_34192);
var inst_34169 = inst_34193;
var inst_34170 = inst_34192;
var inst_34171 = inst_34194;
var inst_34172 = (0);
var state_34229__$1 = (function (){var statearr_34264 = state_34229;
(statearr_34264[(9)] = inst_34170);

(statearr_34264[(20)] = inst_34171);

(statearr_34264[(21)] = inst_34169);

(statearr_34264[(11)] = inst_34172);

return statearr_34264;
})();
var statearr_34265_34334 = state_34229__$1;
(statearr_34265_34334[(2)] = null);

(statearr_34265_34334[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (41))){
var inst_34188 = (state_34229[(25)]);
var inst_34204 = (state_34229[(2)]);
var inst_34205 = cljs.core.next.call(null,inst_34188);
var inst_34169 = inst_34205;
var inst_34170 = null;
var inst_34171 = (0);
var inst_34172 = (0);
var state_34229__$1 = (function (){var statearr_34266 = state_34229;
(statearr_34266[(9)] = inst_34170);

(statearr_34266[(20)] = inst_34171);

(statearr_34266[(21)] = inst_34169);

(statearr_34266[(11)] = inst_34172);

(statearr_34266[(27)] = inst_34204);

return statearr_34266;
})();
var statearr_34267_34335 = state_34229__$1;
(statearr_34267_34335[(2)] = null);

(statearr_34267_34335[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (43))){
var state_34229__$1 = state_34229;
var statearr_34268_34336 = state_34229__$1;
(statearr_34268_34336[(2)] = null);

(statearr_34268_34336[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (29))){
var inst_34213 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34269_34337 = state_34229__$1;
(statearr_34269_34337[(2)] = inst_34213);

(statearr_34269_34337[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (44))){
var inst_34222 = (state_34229[(2)]);
var state_34229__$1 = (function (){var statearr_34270 = state_34229;
(statearr_34270[(28)] = inst_34222);

return statearr_34270;
})();
var statearr_34271_34338 = state_34229__$1;
(statearr_34271_34338[(2)] = null);

(statearr_34271_34338[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (6))){
var inst_34161 = (state_34229[(29)]);
var inst_34160 = cljs.core.deref.call(null,cs);
var inst_34161__$1 = cljs.core.keys.call(null,inst_34160);
var inst_34162 = cljs.core.count.call(null,inst_34161__$1);
var inst_34163 = cljs.core.reset_BANG_.call(null,dctr,inst_34162);
var inst_34168 = cljs.core.seq.call(null,inst_34161__$1);
var inst_34169 = inst_34168;
var inst_34170 = null;
var inst_34171 = (0);
var inst_34172 = (0);
var state_34229__$1 = (function (){var statearr_34272 = state_34229;
(statearr_34272[(9)] = inst_34170);

(statearr_34272[(20)] = inst_34171);

(statearr_34272[(29)] = inst_34161__$1);

(statearr_34272[(21)] = inst_34169);

(statearr_34272[(11)] = inst_34172);

(statearr_34272[(30)] = inst_34163);

return statearr_34272;
})();
var statearr_34273_34339 = state_34229__$1;
(statearr_34273_34339[(2)] = null);

(statearr_34273_34339[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (28))){
var inst_34169 = (state_34229[(21)]);
var inst_34188 = (state_34229[(25)]);
var inst_34188__$1 = cljs.core.seq.call(null,inst_34169);
var state_34229__$1 = (function (){var statearr_34274 = state_34229;
(statearr_34274[(25)] = inst_34188__$1);

return statearr_34274;
})();
if(inst_34188__$1){
var statearr_34275_34340 = state_34229__$1;
(statearr_34275_34340[(1)] = (33));

} else {
var statearr_34276_34341 = state_34229__$1;
(statearr_34276_34341[(1)] = (34));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (25))){
var inst_34171 = (state_34229[(20)]);
var inst_34172 = (state_34229[(11)]);
var inst_34174 = (inst_34172 < inst_34171);
var inst_34175 = inst_34174;
var state_34229__$1 = state_34229;
if(cljs.core.truth_(inst_34175)){
var statearr_34277_34342 = state_34229__$1;
(statearr_34277_34342[(1)] = (27));

} else {
var statearr_34278_34343 = state_34229__$1;
(statearr_34278_34343[(1)] = (28));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (34))){
var state_34229__$1 = state_34229;
var statearr_34279_34344 = state_34229__$1;
(statearr_34279_34344[(2)] = null);

(statearr_34279_34344[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (17))){
var state_34229__$1 = state_34229;
var statearr_34280_34345 = state_34229__$1;
(statearr_34280_34345[(2)] = null);

(statearr_34280_34345[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (3))){
var inst_34227 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34229__$1,inst_34227);
} else {
if((state_val_34230 === (12))){
var inst_34156 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34281_34346 = state_34229__$1;
(statearr_34281_34346[(2)] = inst_34156);

(statearr_34281_34346[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (2))){
var state_34229__$1 = state_34229;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_34229__$1,(4),ch);
} else {
if((state_val_34230 === (23))){
var state_34229__$1 = state_34229;
var statearr_34282_34347 = state_34229__$1;
(statearr_34282_34347[(2)] = null);

(statearr_34282_34347[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (35))){
var inst_34211 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34283_34348 = state_34229__$1;
(statearr_34283_34348[(2)] = inst_34211);

(statearr_34283_34348[(1)] = (29));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (19))){
var inst_34128 = (state_34229[(7)]);
var inst_34132 = cljs.core.chunk_first.call(null,inst_34128);
var inst_34133 = cljs.core.chunk_rest.call(null,inst_34128);
var inst_34134 = cljs.core.count.call(null,inst_34132);
var inst_34106 = inst_34133;
var inst_34107 = inst_34132;
var inst_34108 = inst_34134;
var inst_34109 = (0);
var state_34229__$1 = (function (){var statearr_34284 = state_34229;
(statearr_34284[(13)] = inst_34107);

(statearr_34284[(15)] = inst_34109);

(statearr_34284[(16)] = inst_34106);

(statearr_34284[(17)] = inst_34108);

return statearr_34284;
})();
var statearr_34285_34349 = state_34229__$1;
(statearr_34285_34349[(2)] = null);

(statearr_34285_34349[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (11))){
var inst_34106 = (state_34229[(16)]);
var inst_34128 = (state_34229[(7)]);
var inst_34128__$1 = cljs.core.seq.call(null,inst_34106);
var state_34229__$1 = (function (){var statearr_34286 = state_34229;
(statearr_34286[(7)] = inst_34128__$1);

return statearr_34286;
})();
if(inst_34128__$1){
var statearr_34287_34350 = state_34229__$1;
(statearr_34287_34350[(1)] = (16));

} else {
var statearr_34288_34351 = state_34229__$1;
(statearr_34288_34351[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (9))){
var inst_34158 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34289_34352 = state_34229__$1;
(statearr_34289_34352[(2)] = inst_34158);

(statearr_34289_34352[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (5))){
var inst_34104 = cljs.core.deref.call(null,cs);
var inst_34105 = cljs.core.seq.call(null,inst_34104);
var inst_34106 = inst_34105;
var inst_34107 = null;
var inst_34108 = (0);
var inst_34109 = (0);
var state_34229__$1 = (function (){var statearr_34290 = state_34229;
(statearr_34290[(13)] = inst_34107);

(statearr_34290[(15)] = inst_34109);

(statearr_34290[(16)] = inst_34106);

(statearr_34290[(17)] = inst_34108);

return statearr_34290;
})();
var statearr_34291_34353 = state_34229__$1;
(statearr_34291_34353[(2)] = null);

(statearr_34291_34353[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (14))){
var state_34229__$1 = state_34229;
var statearr_34292_34354 = state_34229__$1;
(statearr_34292_34354[(2)] = null);

(statearr_34292_34354[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (45))){
var inst_34219 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34293_34355 = state_34229__$1;
(statearr_34293_34355[(2)] = inst_34219);

(statearr_34293_34355[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (26))){
var inst_34161 = (state_34229[(29)]);
var inst_34215 = (state_34229[(2)]);
var inst_34216 = cljs.core.seq.call(null,inst_34161);
var state_34229__$1 = (function (){var statearr_34294 = state_34229;
(statearr_34294[(31)] = inst_34215);

return statearr_34294;
})();
if(inst_34216){
var statearr_34295_34356 = state_34229__$1;
(statearr_34295_34356[(1)] = (42));

} else {
var statearr_34296_34357 = state_34229__$1;
(statearr_34296_34357[(1)] = (43));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (16))){
var inst_34128 = (state_34229[(7)]);
var inst_34130 = cljs.core.chunked_seq_QMARK_.call(null,inst_34128);
var state_34229__$1 = state_34229;
if(inst_34130){
var statearr_34297_34358 = state_34229__$1;
(statearr_34297_34358[(1)] = (19));

} else {
var statearr_34298_34359 = state_34229__$1;
(statearr_34298_34359[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (38))){
var inst_34208 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34299_34360 = state_34229__$1;
(statearr_34299_34360[(2)] = inst_34208);

(statearr_34299_34360[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (30))){
var state_34229__$1 = state_34229;
var statearr_34300_34361 = state_34229__$1;
(statearr_34300_34361[(2)] = null);

(statearr_34300_34361[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (10))){
var inst_34107 = (state_34229[(13)]);
var inst_34109 = (state_34229[(15)]);
var inst_34117 = cljs.core._nth.call(null,inst_34107,inst_34109);
var inst_34118 = cljs.core.nth.call(null,inst_34117,(0),null);
var inst_34119 = cljs.core.nth.call(null,inst_34117,(1),null);
var state_34229__$1 = (function (){var statearr_34301 = state_34229;
(statearr_34301[(26)] = inst_34118);

return statearr_34301;
})();
if(cljs.core.truth_(inst_34119)){
var statearr_34302_34362 = state_34229__$1;
(statearr_34302_34362[(1)] = (13));

} else {
var statearr_34303_34363 = state_34229__$1;
(statearr_34303_34363[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (18))){
var inst_34154 = (state_34229[(2)]);
var state_34229__$1 = state_34229;
var statearr_34304_34364 = state_34229__$1;
(statearr_34304_34364[(2)] = inst_34154);

(statearr_34304_34364[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (42))){
var state_34229__$1 = state_34229;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_34229__$1,(45),dchan);
} else {
if((state_val_34230 === (37))){
var inst_34097 = (state_34229[(10)]);
var inst_34188 = (state_34229[(25)]);
var inst_34197 = (state_34229[(23)]);
var inst_34197__$1 = cljs.core.first.call(null,inst_34188);
var inst_34198 = cljs.core.async.put_BANG_.call(null,inst_34197__$1,inst_34097,done);
var state_34229__$1 = (function (){var statearr_34305 = state_34229;
(statearr_34305[(23)] = inst_34197__$1);

return statearr_34305;
})();
if(cljs.core.truth_(inst_34198)){
var statearr_34306_34365 = state_34229__$1;
(statearr_34306_34365[(1)] = (39));

} else {
var statearr_34307_34366 = state_34229__$1;
(statearr_34307_34366[(1)] = (40));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34230 === (8))){
var inst_34109 = (state_34229[(15)]);
var inst_34108 = (state_34229[(17)]);
var inst_34111 = (inst_34109 < inst_34108);
var inst_34112 = inst_34111;
var state_34229__$1 = state_34229;
if(cljs.core.truth_(inst_34112)){
var statearr_34308_34367 = state_34229__$1;
(statearr_34308_34367[(1)] = (10));

} else {
var statearr_34309_34368 = state_34229__$1;
(statearr_34309_34368[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___34314,cs,m,dchan,dctr,done))
;
return ((function (switch__33350__auto__,c__33495__auto___34314,cs,m,dchan,dctr,done){
return (function() {
var cljs$core$async$mult_$_state_machine__33351__auto__ = null;
var cljs$core$async$mult_$_state_machine__33351__auto____0 = (function (){
var statearr_34310 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34310[(0)] = cljs$core$async$mult_$_state_machine__33351__auto__);

(statearr_34310[(1)] = (1));

return statearr_34310;
});
var cljs$core$async$mult_$_state_machine__33351__auto____1 = (function (state_34229){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34229);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34311){if((e34311 instanceof Object)){
var ex__33354__auto__ = e34311;
var statearr_34312_34369 = state_34229;
(statearr_34312_34369[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34229);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34311;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34370 = state_34229;
state_34229 = G__34370;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$mult_$_state_machine__33351__auto__ = function(state_34229){
switch(arguments.length){
case 0:
return cljs$core$async$mult_$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$mult_$_state_machine__33351__auto____1.call(this,state_34229);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mult_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mult_$_state_machine__33351__auto____0;
cljs$core$async$mult_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mult_$_state_machine__33351__auto____1;
return cljs$core$async$mult_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___34314,cs,m,dchan,dctr,done))
})();
var state__33497__auto__ = (function (){var statearr_34313 = f__33496__auto__.call(null);
(statearr_34313[(6)] = c__33495__auto___34314);

return statearr_34313;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___34314,cs,m,dchan,dctr,done))
);


return m;
});
/**
 * Copies the mult source onto the supplied channel.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.tap = (function cljs$core$async$tap(var_args){
var G__34372 = arguments.length;
switch (G__34372) {
case 2:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2 = (function (mult,ch){
return cljs.core.async.tap.call(null,mult,ch,true);
});

cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3 = (function (mult,ch,close_QMARK_){
cljs.core.async.tap_STAR_.call(null,mult,ch,close_QMARK_);

return ch;
});

cljs.core.async.tap.cljs$lang$maxFixedArity = 3;

/**
 * Disconnects a target channel from a mult
 */
cljs.core.async.untap = (function cljs$core$async$untap(mult,ch){
return cljs.core.async.untap_STAR_.call(null,mult,ch);
});
/**
 * Disconnects all target channels from a mult
 */
cljs.core.async.untap_all = (function cljs$core$async$untap_all(mult){
return cljs.core.async.untap_all_STAR_.call(null,mult);
});

/**
 * @interface
 */
cljs.core.async.Mix = function(){};

cljs.core.async.admix_STAR_ = (function cljs$core$async$admix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$admix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$admix_STAR_$arity$2(m,ch);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.admix_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch);
} else {
var m__4431__auto__ = (cljs.core.async.admix_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.admix*",m);
}
}
}
});

cljs.core.async.unmix_STAR_ = (function cljs$core$async$unmix_STAR_(m,ch){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$unmix_STAR_$arity$2(m,ch);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.unmix_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,ch);
} else {
var m__4431__auto__ = (cljs.core.async.unmix_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.unmix*",m);
}
}
}
});

cljs.core.async.unmix_all_STAR_ = (function cljs$core$async$unmix_all_STAR_(m){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$unmix_all_STAR_$arity$1 == null)))))){
return m.cljs$core$async$Mix$unmix_all_STAR_$arity$1(m);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.unmix_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m);
} else {
var m__4431__auto__ = (cljs.core.async.unmix_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.unmix-all*",m);
}
}
}
});

cljs.core.async.toggle_STAR_ = (function cljs$core$async$toggle_STAR_(m,state_map){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$toggle_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$toggle_STAR_$arity$2(m,state_map);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.toggle_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,state_map);
} else {
var m__4431__auto__ = (cljs.core.async.toggle_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,state_map);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.toggle*",m);
}
}
}
});

cljs.core.async.solo_mode_STAR_ = (function cljs$core$async$solo_mode_STAR_(m,mode){
if((((!((m == null)))) && ((!((m.cljs$core$async$Mix$solo_mode_STAR_$arity$2 == null)))))){
return m.cljs$core$async$Mix$solo_mode_STAR_$arity$2(m,mode);
} else {
var x__4433__auto__ = (((m == null))?null:m);
var m__4434__auto__ = (cljs.core.async.solo_mode_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,m,mode);
} else {
var m__4431__auto__ = (cljs.core.async.solo_mode_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,m,mode);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.solo-mode*",m);
}
}
}
});

cljs.core.async.ioc_alts_BANG_ = (function cljs$core$async$ioc_alts_BANG_(var_args){
var args__4736__auto__ = [];
var len__4730__auto___34384 = arguments.length;
var i__4731__auto___34385 = (0);
while(true){
if((i__4731__auto___34385 < len__4730__auto___34384)){
args__4736__auto__.push((arguments[i__4731__auto___34385]));

var G__34386 = (i__4731__auto___34385 + (1));
i__4731__auto___34385 = G__34386;
continue;
} else {
}
break;
}

var argseq__4737__auto__ = ((((3) < args__4736__auto__.length))?(new cljs.core.IndexedSeq(args__4736__auto__.slice((3)),(0),null)):null);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__4737__auto__);
});

cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (state,cont_block,ports,p__34378){
var map__34379 = p__34378;
var map__34379__$1 = (((((!((map__34379 == null))))?(((((map__34379.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__34379.cljs$core$ISeq$))))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__34379):map__34379);
var opts = map__34379__$1;
var statearr_34381_34387 = state;
(statearr_34381_34387[(1)] = cont_block);


var temp__5753__auto__ = cljs.core.async.do_alts.call(null,((function (map__34379,map__34379__$1,opts){
return (function (val){
var statearr_34382_34388 = state;
(statearr_34382_34388[(2)] = val);


return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state);
});})(map__34379,map__34379__$1,opts))
,ports,opts);
if(cljs.core.truth_(temp__5753__auto__)){
var cb = temp__5753__auto__;
var statearr_34383_34389 = state;
(statearr_34383_34389[(2)] = cljs.core.deref.call(null,cb));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
});

cljs.core.async.ioc_alts_BANG_.cljs$lang$maxFixedArity = (3);

/** @this {Function} */
cljs.core.async.ioc_alts_BANG_.cljs$lang$applyTo = (function (seq34374){
var G__34375 = cljs.core.first.call(null,seq34374);
var seq34374__$1 = cljs.core.next.call(null,seq34374);
var G__34376 = cljs.core.first.call(null,seq34374__$1);
var seq34374__$2 = cljs.core.next.call(null,seq34374__$1);
var G__34377 = cljs.core.first.call(null,seq34374__$2);
var seq34374__$3 = cljs.core.next.call(null,seq34374__$2);
var self__4717__auto__ = this;
return self__4717__auto__.cljs$core$IFn$_invoke$arity$variadic(G__34375,G__34376,G__34377,seq34374__$3);
});

/**
 * Creates and returns a mix of one or more input channels which will
 *   be put on the supplied out channel. Input sources can be added to
 *   the mix with 'admix', and removed with 'unmix'. A mix supports
 *   soloing, muting and pausing multiple inputs atomically using
 *   'toggle', and can solo using either muting or pausing as determined
 *   by 'solo-mode'.
 * 
 *   Each channel can have zero or more boolean modes set via 'toggle':
 * 
 *   :solo - when true, only this (ond other soloed) channel(s) will appear
 *        in the mix output channel. :mute and :pause states of soloed
 *        channels are ignored. If solo-mode is :mute, non-soloed
 *        channels are muted, if :pause, non-soloed channels are
 *        paused.
 * 
 *   :mute - muted channels will have their contents consumed but not included in the mix
 *   :pause - paused channels will not have their contents consumed (and thus also not included in the mix)
 */
cljs.core.async.mix = (function cljs$core$async$mix(out){
var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var solo_modes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pause","pause",-2095325672),null,new cljs.core.Keyword(null,"mute","mute",1151223646),null], null), null);
var attrs = cljs.core.conj.call(null,solo_modes,new cljs.core.Keyword(null,"solo","solo",-316350075));
var solo_mode = cljs.core.atom.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646));
var change = cljs.core.async.chan.call(null);
var changed = ((function (cs,solo_modes,attrs,solo_mode,change){
return (function (){
return cljs.core.async.put_BANG_.call(null,change,true);
});})(cs,solo_modes,attrs,solo_mode,change))
;
var pick = ((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (attr,chs){
return cljs.core.reduce_kv.call(null,((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (ret,c,v){
if(cljs.core.truth_(attr.call(null,v))){
return cljs.core.conj.call(null,ret,c);
} else {
return ret;
}
});})(cs,solo_modes,attrs,solo_mode,change,changed))
,cljs.core.PersistentHashSet.EMPTY,chs);
});})(cs,solo_modes,attrs,solo_mode,change,changed))
;
var calc_state = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick){
return (function (){
var chs = cljs.core.deref.call(null,cs);
var mode = cljs.core.deref.call(null,solo_mode);
var solos = pick.call(null,new cljs.core.Keyword(null,"solo","solo",-316350075),chs);
var pauses = pick.call(null,new cljs.core.Keyword(null,"pause","pause",-2095325672),chs);
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"solos","solos",1441458643),solos,new cljs.core.Keyword(null,"mutes","mutes",1068806309),pick.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646),chs),new cljs.core.Keyword(null,"reads","reads",-1215067361),cljs.core.conj.call(null,((((cljs.core._EQ_.call(null,mode,new cljs.core.Keyword(null,"pause","pause",-2095325672))) && ((!(cljs.core.empty_QMARK_.call(null,solos))))))?cljs.core.vec.call(null,solos):cljs.core.vec.call(null,cljs.core.remove.call(null,pauses,cljs.core.keys.call(null,chs)))),change)], null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick))
;
var m = (function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async34390 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mix}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34390 = (function (change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta34391){
this.change = change;
this.solo_mode = solo_mode;
this.pick = pick;
this.cs = cs;
this.calc_state = calc_state;
this.out = out;
this.changed = changed;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.meta34391 = meta34391;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_34392,meta34391__$1){
var self__ = this;
var _34392__$1 = this;
return (new cljs.core.async.t_cljs$core$async34390(self__.change,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta34391__$1));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_34392){
var self__ = this;
var _34392__$1 = this;
return self__.meta34391;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.out;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mix$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,state_map){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.partial.call(null,cljs.core.merge_with,cljs.core.merge),state_map);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,mode){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_(self__.solo_modes.call(null,mode))){
} else {
throw (new Error(["Assert failed: ",["mode must be one of: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)].join(''),"\n","(solo-modes mode)"].join('')));
}

cljs.core.reset_BANG_.call(null,self__.solo_mode,mode);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.getBasis = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (){
return new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"change","change",477485025,null),new cljs.core.Symbol(null,"solo-mode","solo-mode",2031788074,null),new cljs.core.Symbol(null,"pick","pick",1300068175,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"calc-state","calc-state",-349968968,null),new cljs.core.Symbol(null,"out","out",729986010,null),new cljs.core.Symbol(null,"changed","changed",-2083710852,null),new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"attrs","attrs",-450137186,null),new cljs.core.Symbol(null,"meta34391","meta34391",-962299627,null)], null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async34390.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async34390.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34390";

cljs.core.async.t_cljs$core$async34390.cljs$lang$ctorPrWriter = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async34390");
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34390.
 */
cljs.core.async.__GT_t_cljs$core$async34390 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function cljs$core$async$mix_$___GT_t_cljs$core$async34390(change__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta34391){
return (new cljs.core.async.t_cljs$core$async34390(change__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta34391));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

}

return (new cljs.core.async.t_cljs$core$async34390(change,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var c__33495__auto___34554 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___34554,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___34554,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (state_34494){
var state_val_34495 = (state_34494[(1)]);
if((state_val_34495 === (7))){
var inst_34409 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
var statearr_34496_34555 = state_34494__$1;
(statearr_34496_34555[(2)] = inst_34409);

(statearr_34496_34555[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (20))){
var inst_34421 = (state_34494[(7)]);
var state_34494__$1 = state_34494;
var statearr_34497_34556 = state_34494__$1;
(statearr_34497_34556[(2)] = inst_34421);

(statearr_34497_34556[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (27))){
var state_34494__$1 = state_34494;
var statearr_34498_34557 = state_34494__$1;
(statearr_34498_34557[(2)] = null);

(statearr_34498_34557[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (1))){
var inst_34396 = (state_34494[(8)]);
var inst_34396__$1 = calc_state.call(null);
var inst_34398 = (inst_34396__$1 == null);
var inst_34399 = cljs.core.not.call(null,inst_34398);
var state_34494__$1 = (function (){var statearr_34499 = state_34494;
(statearr_34499[(8)] = inst_34396__$1);

return statearr_34499;
})();
if(inst_34399){
var statearr_34500_34558 = state_34494__$1;
(statearr_34500_34558[(1)] = (2));

} else {
var statearr_34501_34559 = state_34494__$1;
(statearr_34501_34559[(1)] = (3));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (24))){
var inst_34454 = (state_34494[(9)]);
var inst_34468 = (state_34494[(10)]);
var inst_34445 = (state_34494[(11)]);
var inst_34468__$1 = inst_34445.call(null,inst_34454);
var state_34494__$1 = (function (){var statearr_34502 = state_34494;
(statearr_34502[(10)] = inst_34468__$1);

return statearr_34502;
})();
if(cljs.core.truth_(inst_34468__$1)){
var statearr_34503_34560 = state_34494__$1;
(statearr_34503_34560[(1)] = (29));

} else {
var statearr_34504_34561 = state_34494__$1;
(statearr_34504_34561[(1)] = (30));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (4))){
var inst_34412 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
if(cljs.core.truth_(inst_34412)){
var statearr_34505_34562 = state_34494__$1;
(statearr_34505_34562[(1)] = (8));

} else {
var statearr_34506_34563 = state_34494__$1;
(statearr_34506_34563[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (15))){
var inst_34439 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
if(cljs.core.truth_(inst_34439)){
var statearr_34507_34564 = state_34494__$1;
(statearr_34507_34564[(1)] = (19));

} else {
var statearr_34508_34565 = state_34494__$1;
(statearr_34508_34565[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (21))){
var inst_34444 = (state_34494[(12)]);
var inst_34444__$1 = (state_34494[(2)]);
var inst_34445 = cljs.core.get.call(null,inst_34444__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_34446 = cljs.core.get.call(null,inst_34444__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_34447 = cljs.core.get.call(null,inst_34444__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var state_34494__$1 = (function (){var statearr_34509 = state_34494;
(statearr_34509[(11)] = inst_34445);

(statearr_34509[(12)] = inst_34444__$1);

(statearr_34509[(13)] = inst_34446);

return statearr_34509;
})();
return cljs.core.async.ioc_alts_BANG_.call(null,state_34494__$1,(22),inst_34447);
} else {
if((state_val_34495 === (31))){
var inst_34476 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
if(cljs.core.truth_(inst_34476)){
var statearr_34510_34566 = state_34494__$1;
(statearr_34510_34566[(1)] = (32));

} else {
var statearr_34511_34567 = state_34494__$1;
(statearr_34511_34567[(1)] = (33));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (32))){
var inst_34453 = (state_34494[(14)]);
var state_34494__$1 = state_34494;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_34494__$1,(35),out,inst_34453);
} else {
if((state_val_34495 === (33))){
var inst_34444 = (state_34494[(12)]);
var inst_34421 = inst_34444;
var state_34494__$1 = (function (){var statearr_34512 = state_34494;
(statearr_34512[(7)] = inst_34421);

return statearr_34512;
})();
var statearr_34513_34568 = state_34494__$1;
(statearr_34513_34568[(2)] = null);

(statearr_34513_34568[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (13))){
var inst_34421 = (state_34494[(7)]);
var inst_34428 = inst_34421.cljs$lang$protocol_mask$partition0$;
var inst_34429 = (inst_34428 & (64));
var inst_34430 = inst_34421.cljs$core$ISeq$;
var inst_34431 = (cljs.core.PROTOCOL_SENTINEL === inst_34430);
var inst_34432 = ((inst_34429) || (inst_34431));
var state_34494__$1 = state_34494;
if(cljs.core.truth_(inst_34432)){
var statearr_34514_34569 = state_34494__$1;
(statearr_34514_34569[(1)] = (16));

} else {
var statearr_34515_34570 = state_34494__$1;
(statearr_34515_34570[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (22))){
var inst_34454 = (state_34494[(9)]);
var inst_34453 = (state_34494[(14)]);
var inst_34452 = (state_34494[(2)]);
var inst_34453__$1 = cljs.core.nth.call(null,inst_34452,(0),null);
var inst_34454__$1 = cljs.core.nth.call(null,inst_34452,(1),null);
var inst_34455 = (inst_34453__$1 == null);
var inst_34456 = cljs.core._EQ_.call(null,inst_34454__$1,change);
var inst_34457 = ((inst_34455) || (inst_34456));
var state_34494__$1 = (function (){var statearr_34516 = state_34494;
(statearr_34516[(9)] = inst_34454__$1);

(statearr_34516[(14)] = inst_34453__$1);

return statearr_34516;
})();
if(cljs.core.truth_(inst_34457)){
var statearr_34517_34571 = state_34494__$1;
(statearr_34517_34571[(1)] = (23));

} else {
var statearr_34518_34572 = state_34494__$1;
(statearr_34518_34572[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (36))){
var inst_34444 = (state_34494[(12)]);
var inst_34421 = inst_34444;
var state_34494__$1 = (function (){var statearr_34519 = state_34494;
(statearr_34519[(7)] = inst_34421);

return statearr_34519;
})();
var statearr_34520_34573 = state_34494__$1;
(statearr_34520_34573[(2)] = null);

(statearr_34520_34573[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (29))){
var inst_34468 = (state_34494[(10)]);
var state_34494__$1 = state_34494;
var statearr_34521_34574 = state_34494__$1;
(statearr_34521_34574[(2)] = inst_34468);

(statearr_34521_34574[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (6))){
var state_34494__$1 = state_34494;
var statearr_34522_34575 = state_34494__$1;
(statearr_34522_34575[(2)] = false);

(statearr_34522_34575[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (28))){
var inst_34464 = (state_34494[(2)]);
var inst_34465 = calc_state.call(null);
var inst_34421 = inst_34465;
var state_34494__$1 = (function (){var statearr_34523 = state_34494;
(statearr_34523[(15)] = inst_34464);

(statearr_34523[(7)] = inst_34421);

return statearr_34523;
})();
var statearr_34524_34576 = state_34494__$1;
(statearr_34524_34576[(2)] = null);

(statearr_34524_34576[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (25))){
var inst_34490 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
var statearr_34525_34577 = state_34494__$1;
(statearr_34525_34577[(2)] = inst_34490);

(statearr_34525_34577[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (34))){
var inst_34488 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
var statearr_34526_34578 = state_34494__$1;
(statearr_34526_34578[(2)] = inst_34488);

(statearr_34526_34578[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (17))){
var state_34494__$1 = state_34494;
var statearr_34527_34579 = state_34494__$1;
(statearr_34527_34579[(2)] = false);

(statearr_34527_34579[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (3))){
var state_34494__$1 = state_34494;
var statearr_34528_34580 = state_34494__$1;
(statearr_34528_34580[(2)] = false);

(statearr_34528_34580[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (12))){
var inst_34492 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34494__$1,inst_34492);
} else {
if((state_val_34495 === (2))){
var inst_34396 = (state_34494[(8)]);
var inst_34401 = inst_34396.cljs$lang$protocol_mask$partition0$;
var inst_34402 = (inst_34401 & (64));
var inst_34403 = inst_34396.cljs$core$ISeq$;
var inst_34404 = (cljs.core.PROTOCOL_SENTINEL === inst_34403);
var inst_34405 = ((inst_34402) || (inst_34404));
var state_34494__$1 = state_34494;
if(cljs.core.truth_(inst_34405)){
var statearr_34529_34581 = state_34494__$1;
(statearr_34529_34581[(1)] = (5));

} else {
var statearr_34530_34582 = state_34494__$1;
(statearr_34530_34582[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (23))){
var inst_34453 = (state_34494[(14)]);
var inst_34459 = (inst_34453 == null);
var state_34494__$1 = state_34494;
if(cljs.core.truth_(inst_34459)){
var statearr_34531_34583 = state_34494__$1;
(statearr_34531_34583[(1)] = (26));

} else {
var statearr_34532_34584 = state_34494__$1;
(statearr_34532_34584[(1)] = (27));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (35))){
var inst_34479 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
if(cljs.core.truth_(inst_34479)){
var statearr_34533_34585 = state_34494__$1;
(statearr_34533_34585[(1)] = (36));

} else {
var statearr_34534_34586 = state_34494__$1;
(statearr_34534_34586[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (19))){
var inst_34421 = (state_34494[(7)]);
var inst_34441 = cljs.core.apply.call(null,cljs.core.hash_map,inst_34421);
var state_34494__$1 = state_34494;
var statearr_34535_34587 = state_34494__$1;
(statearr_34535_34587[(2)] = inst_34441);

(statearr_34535_34587[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (11))){
var inst_34421 = (state_34494[(7)]);
var inst_34425 = (inst_34421 == null);
var inst_34426 = cljs.core.not.call(null,inst_34425);
var state_34494__$1 = state_34494;
if(inst_34426){
var statearr_34536_34588 = state_34494__$1;
(statearr_34536_34588[(1)] = (13));

} else {
var statearr_34537_34589 = state_34494__$1;
(statearr_34537_34589[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (9))){
var inst_34396 = (state_34494[(8)]);
var state_34494__$1 = state_34494;
var statearr_34538_34590 = state_34494__$1;
(statearr_34538_34590[(2)] = inst_34396);

(statearr_34538_34590[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (5))){
var state_34494__$1 = state_34494;
var statearr_34539_34591 = state_34494__$1;
(statearr_34539_34591[(2)] = true);

(statearr_34539_34591[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (14))){
var state_34494__$1 = state_34494;
var statearr_34540_34592 = state_34494__$1;
(statearr_34540_34592[(2)] = false);

(statearr_34540_34592[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (26))){
var inst_34454 = (state_34494[(9)]);
var inst_34461 = cljs.core.swap_BANG_.call(null,cs,cljs.core.dissoc,inst_34454);
var state_34494__$1 = state_34494;
var statearr_34541_34593 = state_34494__$1;
(statearr_34541_34593[(2)] = inst_34461);

(statearr_34541_34593[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (16))){
var state_34494__$1 = state_34494;
var statearr_34542_34594 = state_34494__$1;
(statearr_34542_34594[(2)] = true);

(statearr_34542_34594[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (38))){
var inst_34484 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
var statearr_34543_34595 = state_34494__$1;
(statearr_34543_34595[(2)] = inst_34484);

(statearr_34543_34595[(1)] = (34));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (30))){
var inst_34454 = (state_34494[(9)]);
var inst_34445 = (state_34494[(11)]);
var inst_34446 = (state_34494[(13)]);
var inst_34471 = cljs.core.empty_QMARK_.call(null,inst_34445);
var inst_34472 = inst_34446.call(null,inst_34454);
var inst_34473 = cljs.core.not.call(null,inst_34472);
var inst_34474 = ((inst_34471) && (inst_34473));
var state_34494__$1 = state_34494;
var statearr_34544_34596 = state_34494__$1;
(statearr_34544_34596[(2)] = inst_34474);

(statearr_34544_34596[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (10))){
var inst_34396 = (state_34494[(8)]);
var inst_34417 = (state_34494[(2)]);
var inst_34418 = cljs.core.get.call(null,inst_34417,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_34419 = cljs.core.get.call(null,inst_34417,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_34420 = cljs.core.get.call(null,inst_34417,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var inst_34421 = inst_34396;
var state_34494__$1 = (function (){var statearr_34545 = state_34494;
(statearr_34545[(7)] = inst_34421);

(statearr_34545[(16)] = inst_34420);

(statearr_34545[(17)] = inst_34418);

(statearr_34545[(18)] = inst_34419);

return statearr_34545;
})();
var statearr_34546_34597 = state_34494__$1;
(statearr_34546_34597[(2)] = null);

(statearr_34546_34597[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (18))){
var inst_34436 = (state_34494[(2)]);
var state_34494__$1 = state_34494;
var statearr_34547_34598 = state_34494__$1;
(statearr_34547_34598[(2)] = inst_34436);

(statearr_34547_34598[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (37))){
var state_34494__$1 = state_34494;
var statearr_34548_34599 = state_34494__$1;
(statearr_34548_34599[(2)] = null);

(statearr_34548_34599[(1)] = (38));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34495 === (8))){
var inst_34396 = (state_34494[(8)]);
var inst_34414 = cljs.core.apply.call(null,cljs.core.hash_map,inst_34396);
var state_34494__$1 = state_34494;
var statearr_34549_34600 = state_34494__$1;
(statearr_34549_34600[(2)] = inst_34414);

(statearr_34549_34600[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___34554,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
;
return ((function (switch__33350__auto__,c__33495__auto___34554,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function() {
var cljs$core$async$mix_$_state_machine__33351__auto__ = null;
var cljs$core$async$mix_$_state_machine__33351__auto____0 = (function (){
var statearr_34550 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34550[(0)] = cljs$core$async$mix_$_state_machine__33351__auto__);

(statearr_34550[(1)] = (1));

return statearr_34550;
});
var cljs$core$async$mix_$_state_machine__33351__auto____1 = (function (state_34494){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34494);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34551){if((e34551 instanceof Object)){
var ex__33354__auto__ = e34551;
var statearr_34552_34601 = state_34494;
(statearr_34552_34601[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34494);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34551;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34602 = state_34494;
state_34494 = G__34602;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$mix_$_state_machine__33351__auto__ = function(state_34494){
switch(arguments.length){
case 0:
return cljs$core$async$mix_$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$mix_$_state_machine__33351__auto____1.call(this,state_34494);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mix_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mix_$_state_machine__33351__auto____0;
cljs$core$async$mix_$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mix_$_state_machine__33351__auto____1;
return cljs$core$async$mix_$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___34554,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
})();
var state__33497__auto__ = (function (){var statearr_34553 = f__33496__auto__.call(null);
(statearr_34553[(6)] = c__33495__auto___34554);

return statearr_34553;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___34554,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
);


return m;
});
/**
 * Adds ch as an input to the mix
 */
cljs.core.async.admix = (function cljs$core$async$admix(mix,ch){
return cljs.core.async.admix_STAR_.call(null,mix,ch);
});
/**
 * Removes ch as an input to the mix
 */
cljs.core.async.unmix = (function cljs$core$async$unmix(mix,ch){
return cljs.core.async.unmix_STAR_.call(null,mix,ch);
});
/**
 * removes all inputs from the mix
 */
cljs.core.async.unmix_all = (function cljs$core$async$unmix_all(mix){
return cljs.core.async.unmix_all_STAR_.call(null,mix);
});
/**
 * Atomically sets the state(s) of one or more channels in a mix. The
 *   state map is a map of channels -> channel-state-map. A
 *   channel-state-map is a map of attrs -> boolean, where attr is one or
 *   more of :mute, :pause or :solo. Any states supplied are merged with
 *   the current state.
 * 
 *   Note that channels can be added to a mix via toggle, which can be
 *   used to add channels in a particular (e.g. paused) state.
 */
cljs.core.async.toggle = (function cljs$core$async$toggle(mix,state_map){
return cljs.core.async.toggle_STAR_.call(null,mix,state_map);
});
/**
 * Sets the solo mode of the mix. mode must be one of :mute or :pause
 */
cljs.core.async.solo_mode = (function cljs$core$async$solo_mode(mix,mode){
return cljs.core.async.solo_mode_STAR_.call(null,mix,mode);
});

/**
 * @interface
 */
cljs.core.async.Pub = function(){};

cljs.core.async.sub_STAR_ = (function cljs$core$async$sub_STAR_(p,v,ch,close_QMARK_){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$sub_STAR_$arity$4 == null)))))){
return p.cljs$core$async$Pub$sub_STAR_$arity$4(p,v,ch,close_QMARK_);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.sub_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p,v,ch,close_QMARK_);
} else {
var m__4431__auto__ = (cljs.core.async.sub_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p,v,ch,close_QMARK_);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.sub*",p);
}
}
}
});

cljs.core.async.unsub_STAR_ = (function cljs$core$async$unsub_STAR_(p,v,ch){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_STAR_$arity$3 == null)))))){
return p.cljs$core$async$Pub$unsub_STAR_$arity$3(p,v,ch);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.unsub_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p,v,ch);
} else {
var m__4431__auto__ = (cljs.core.async.unsub_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p,v,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_ = (function cljs$core$async$unsub_all_STAR_(var_args){
var G__34604 = arguments.length;
switch (G__34604) {
case 1:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1 = (function (p){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$1 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$1(p);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p);
} else {
var m__4431__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (p,v){
if((((!((p == null)))) && ((!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$2 == null)))))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else {
var x__4433__auto__ = (((p == null))?null:p);
var m__4434__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__4433__auto__)]);
if((!((m__4434__auto__ == null)))){
return m__4434__auto__.call(null,p,v);
} else {
var m__4431__auto__ = (cljs.core.async.unsub_all_STAR_["_"]);
if((!((m__4431__auto__ == null)))){
return m__4431__auto__.call(null,p,v);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_.cljs$lang$maxFixedArity = 2;


/**
 * Creates and returns a pub(lication) of the supplied channel,
 *   partitioned into topics by the topic-fn. topic-fn will be applied to
 *   each value on the channel and the result will determine the 'topic'
 *   on which that value will be put. Channels can be subscribed to
 *   receive copies of topics using 'sub', and unsubscribed using
 *   'unsub'. Each topic will be handled by an internal mult on a
 *   dedicated channel. By default these internal channels are
 *   unbuffered, but a buf-fn can be supplied which, given a topic,
 *   creates a buffer with desired properties.
 * 
 *   Each item is distributed to all subs in parallel and synchronously,
 *   i.e. each sub must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow subs from holding up the pub.
 * 
 *   Items received when there are no matching subs get dropped.
 * 
 *   Note that if buf-fns are used then each topic is handled
 *   asynchronously, i.e. if a channel is subscribed to more than one
 *   topic it should not expect them to be interleaved identically with
 *   the source.
 */
cljs.core.async.pub = (function cljs$core$async$pub(var_args){
var G__34608 = arguments.length;
switch (G__34608) {
case 2:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2 = (function (ch,topic_fn){
return cljs.core.async.pub.call(null,ch,topic_fn,cljs.core.constantly.call(null,null));
});

cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3 = (function (ch,topic_fn,buf_fn){
var mults = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var ensure_mult = ((function (mults){
return (function (topic){
var or__4131__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,mults),topic);
if(cljs.core.truth_(or__4131__auto__)){
return or__4131__auto__;
} else {
return cljs.core.get.call(null,cljs.core.swap_BANG_.call(null,mults,((function (or__4131__auto__,mults){
return (function (p1__34606_SHARP_){
if(cljs.core.truth_(p1__34606_SHARP_.call(null,topic))){
return p1__34606_SHARP_;
} else {
return cljs.core.assoc.call(null,p1__34606_SHARP_,topic,cljs.core.async.mult.call(null,cljs.core.async.chan.call(null,buf_fn.call(null,topic))));
}
});})(or__4131__auto__,mults))
),topic);
}
});})(mults))
;
var p = (function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async34609 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.Pub}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34609 = (function (ch,topic_fn,buf_fn,mults,ensure_mult,meta34610){
this.ch = ch;
this.topic_fn = topic_fn;
this.buf_fn = buf_fn;
this.mults = mults;
this.ensure_mult = ensure_mult;
this.meta34610 = meta34610;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (mults,ensure_mult){
return (function (_34611,meta34610__$1){
var self__ = this;
var _34611__$1 = this;
return (new cljs.core.async.t_cljs$core$async34609(self__.ch,self__.topic_fn,self__.buf_fn,self__.mults,self__.ensure_mult,meta34610__$1));
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (mults,ensure_mult){
return (function (_34611){
var self__ = this;
var _34611__$1 = this;
return self__.meta34610;
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$async$Pub$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$1,close_QMARK_){
var self__ = this;
var p__$1 = this;
var m = self__.ensure_mult.call(null,topic);
return cljs.core.async.tap.call(null,m,ch__$1,close_QMARK_);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$1){
var self__ = this;
var p__$1 = this;
var temp__5753__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,self__.mults),topic);
if(cljs.core.truth_(temp__5753__auto__)){
var m = temp__5753__auto__;
return cljs.core.async.untap.call(null,m,ch__$1);
} else {
return null;
}
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.reset_BANG_.call(null,self__.mults,cljs.core.PersistentArrayMap.EMPTY);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = ((function (mults,ensure_mult){
return (function (_,topic){
var self__ = this;
var ___$1 = this;
return cljs.core.swap_BANG_.call(null,self__.mults,cljs.core.dissoc,topic);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.getBasis = ((function (mults,ensure_mult){
return (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"topic-fn","topic-fn",-862449736,null),new cljs.core.Symbol(null,"buf-fn","buf-fn",-1200281591,null),new cljs.core.Symbol(null,"mults","mults",-461114485,null),new cljs.core.Symbol(null,"ensure-mult","ensure-mult",1796584816,null),new cljs.core.Symbol(null,"meta34610","meta34610",-1649044442,null)], null);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async34609.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async34609.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34609";

cljs.core.async.t_cljs$core$async34609.cljs$lang$ctorPrWriter = ((function (mults,ensure_mult){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async34609");
});})(mults,ensure_mult))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34609.
 */
cljs.core.async.__GT_t_cljs$core$async34609 = ((function (mults,ensure_mult){
return (function cljs$core$async$__GT_t_cljs$core$async34609(ch__$1,topic_fn__$1,buf_fn__$1,mults__$1,ensure_mult__$1,meta34610){
return (new cljs.core.async.t_cljs$core$async34609(ch__$1,topic_fn__$1,buf_fn__$1,mults__$1,ensure_mult__$1,meta34610));
});})(mults,ensure_mult))
;

}

return (new cljs.core.async.t_cljs$core$async34609(ch,topic_fn,buf_fn,mults,ensure_mult,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var c__33495__auto___34729 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___34729,mults,ensure_mult,p){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___34729,mults,ensure_mult,p){
return (function (state_34683){
var state_val_34684 = (state_34683[(1)]);
if((state_val_34684 === (7))){
var inst_34679 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
var statearr_34685_34730 = state_34683__$1;
(statearr_34685_34730[(2)] = inst_34679);

(statearr_34685_34730[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (20))){
var state_34683__$1 = state_34683;
var statearr_34686_34731 = state_34683__$1;
(statearr_34686_34731[(2)] = null);

(statearr_34686_34731[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (1))){
var state_34683__$1 = state_34683;
var statearr_34687_34732 = state_34683__$1;
(statearr_34687_34732[(2)] = null);

(statearr_34687_34732[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (24))){
var inst_34662 = (state_34683[(7)]);
var inst_34671 = cljs.core.swap_BANG_.call(null,mults,cljs.core.dissoc,inst_34662);
var state_34683__$1 = state_34683;
var statearr_34688_34733 = state_34683__$1;
(statearr_34688_34733[(2)] = inst_34671);

(statearr_34688_34733[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (4))){
var inst_34614 = (state_34683[(8)]);
var inst_34614__$1 = (state_34683[(2)]);
var inst_34615 = (inst_34614__$1 == null);
var state_34683__$1 = (function (){var statearr_34689 = state_34683;
(statearr_34689[(8)] = inst_34614__$1);

return statearr_34689;
})();
if(cljs.core.truth_(inst_34615)){
var statearr_34690_34734 = state_34683__$1;
(statearr_34690_34734[(1)] = (5));

} else {
var statearr_34691_34735 = state_34683__$1;
(statearr_34691_34735[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (15))){
var inst_34656 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
var statearr_34692_34736 = state_34683__$1;
(statearr_34692_34736[(2)] = inst_34656);

(statearr_34692_34736[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (21))){
var inst_34676 = (state_34683[(2)]);
var state_34683__$1 = (function (){var statearr_34693 = state_34683;
(statearr_34693[(9)] = inst_34676);

return statearr_34693;
})();
var statearr_34694_34737 = state_34683__$1;
(statearr_34694_34737[(2)] = null);

(statearr_34694_34737[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (13))){
var inst_34638 = (state_34683[(10)]);
var inst_34640 = cljs.core.chunked_seq_QMARK_.call(null,inst_34638);
var state_34683__$1 = state_34683;
if(inst_34640){
var statearr_34695_34738 = state_34683__$1;
(statearr_34695_34738[(1)] = (16));

} else {
var statearr_34696_34739 = state_34683__$1;
(statearr_34696_34739[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (22))){
var inst_34668 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
if(cljs.core.truth_(inst_34668)){
var statearr_34697_34740 = state_34683__$1;
(statearr_34697_34740[(1)] = (23));

} else {
var statearr_34698_34741 = state_34683__$1;
(statearr_34698_34741[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (6))){
var inst_34614 = (state_34683[(8)]);
var inst_34664 = (state_34683[(11)]);
var inst_34662 = (state_34683[(7)]);
var inst_34662__$1 = topic_fn.call(null,inst_34614);
var inst_34663 = cljs.core.deref.call(null,mults);
var inst_34664__$1 = cljs.core.get.call(null,inst_34663,inst_34662__$1);
var state_34683__$1 = (function (){var statearr_34699 = state_34683;
(statearr_34699[(11)] = inst_34664__$1);

(statearr_34699[(7)] = inst_34662__$1);

return statearr_34699;
})();
if(cljs.core.truth_(inst_34664__$1)){
var statearr_34700_34742 = state_34683__$1;
(statearr_34700_34742[(1)] = (19));

} else {
var statearr_34701_34743 = state_34683__$1;
(statearr_34701_34743[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (25))){
var inst_34673 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
var statearr_34702_34744 = state_34683__$1;
(statearr_34702_34744[(2)] = inst_34673);

(statearr_34702_34744[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (17))){
var inst_34638 = (state_34683[(10)]);
var inst_34647 = cljs.core.first.call(null,inst_34638);
var inst_34648 = cljs.core.async.muxch_STAR_.call(null,inst_34647);
var inst_34649 = cljs.core.async.close_BANG_.call(null,inst_34648);
var inst_34650 = cljs.core.next.call(null,inst_34638);
var inst_34624 = inst_34650;
var inst_34625 = null;
var inst_34626 = (0);
var inst_34627 = (0);
var state_34683__$1 = (function (){var statearr_34703 = state_34683;
(statearr_34703[(12)] = inst_34624);

(statearr_34703[(13)] = inst_34625);

(statearr_34703[(14)] = inst_34627);

(statearr_34703[(15)] = inst_34626);

(statearr_34703[(16)] = inst_34649);

return statearr_34703;
})();
var statearr_34704_34745 = state_34683__$1;
(statearr_34704_34745[(2)] = null);

(statearr_34704_34745[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (3))){
var inst_34681 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34683__$1,inst_34681);
} else {
if((state_val_34684 === (12))){
var inst_34658 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
var statearr_34705_34746 = state_34683__$1;
(statearr_34705_34746[(2)] = inst_34658);

(statearr_34705_34746[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (2))){
var state_34683__$1 = state_34683;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_34683__$1,(4),ch);
} else {
if((state_val_34684 === (23))){
var state_34683__$1 = state_34683;
var statearr_34706_34747 = state_34683__$1;
(statearr_34706_34747[(2)] = null);

(statearr_34706_34747[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (19))){
var inst_34614 = (state_34683[(8)]);
var inst_34664 = (state_34683[(11)]);
var inst_34666 = cljs.core.async.muxch_STAR_.call(null,inst_34664);
var state_34683__$1 = state_34683;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_34683__$1,(22),inst_34666,inst_34614);
} else {
if((state_val_34684 === (11))){
var inst_34624 = (state_34683[(12)]);
var inst_34638 = (state_34683[(10)]);
var inst_34638__$1 = cljs.core.seq.call(null,inst_34624);
var state_34683__$1 = (function (){var statearr_34707 = state_34683;
(statearr_34707[(10)] = inst_34638__$1);

return statearr_34707;
})();
if(inst_34638__$1){
var statearr_34708_34748 = state_34683__$1;
(statearr_34708_34748[(1)] = (13));

} else {
var statearr_34709_34749 = state_34683__$1;
(statearr_34709_34749[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (9))){
var inst_34660 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
var statearr_34710_34750 = state_34683__$1;
(statearr_34710_34750[(2)] = inst_34660);

(statearr_34710_34750[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (5))){
var inst_34621 = cljs.core.deref.call(null,mults);
var inst_34622 = cljs.core.vals.call(null,inst_34621);
var inst_34623 = cljs.core.seq.call(null,inst_34622);
var inst_34624 = inst_34623;
var inst_34625 = null;
var inst_34626 = (0);
var inst_34627 = (0);
var state_34683__$1 = (function (){var statearr_34711 = state_34683;
(statearr_34711[(12)] = inst_34624);

(statearr_34711[(13)] = inst_34625);

(statearr_34711[(14)] = inst_34627);

(statearr_34711[(15)] = inst_34626);

return statearr_34711;
})();
var statearr_34712_34751 = state_34683__$1;
(statearr_34712_34751[(2)] = null);

(statearr_34712_34751[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (14))){
var state_34683__$1 = state_34683;
var statearr_34716_34752 = state_34683__$1;
(statearr_34716_34752[(2)] = null);

(statearr_34716_34752[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (16))){
var inst_34638 = (state_34683[(10)]);
var inst_34642 = cljs.core.chunk_first.call(null,inst_34638);
var inst_34643 = cljs.core.chunk_rest.call(null,inst_34638);
var inst_34644 = cljs.core.count.call(null,inst_34642);
var inst_34624 = inst_34643;
var inst_34625 = inst_34642;
var inst_34626 = inst_34644;
var inst_34627 = (0);
var state_34683__$1 = (function (){var statearr_34717 = state_34683;
(statearr_34717[(12)] = inst_34624);

(statearr_34717[(13)] = inst_34625);

(statearr_34717[(14)] = inst_34627);

(statearr_34717[(15)] = inst_34626);

return statearr_34717;
})();
var statearr_34718_34753 = state_34683__$1;
(statearr_34718_34753[(2)] = null);

(statearr_34718_34753[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (10))){
var inst_34624 = (state_34683[(12)]);
var inst_34625 = (state_34683[(13)]);
var inst_34627 = (state_34683[(14)]);
var inst_34626 = (state_34683[(15)]);
var inst_34632 = cljs.core._nth.call(null,inst_34625,inst_34627);
var inst_34633 = cljs.core.async.muxch_STAR_.call(null,inst_34632);
var inst_34634 = cljs.core.async.close_BANG_.call(null,inst_34633);
var inst_34635 = (inst_34627 + (1));
var tmp34713 = inst_34624;
var tmp34714 = inst_34625;
var tmp34715 = inst_34626;
var inst_34624__$1 = tmp34713;
var inst_34625__$1 = tmp34714;
var inst_34626__$1 = tmp34715;
var inst_34627__$1 = inst_34635;
var state_34683__$1 = (function (){var statearr_34719 = state_34683;
(statearr_34719[(12)] = inst_34624__$1);

(statearr_34719[(13)] = inst_34625__$1);

(statearr_34719[(14)] = inst_34627__$1);

(statearr_34719[(15)] = inst_34626__$1);

(statearr_34719[(17)] = inst_34634);

return statearr_34719;
})();
var statearr_34720_34754 = state_34683__$1;
(statearr_34720_34754[(2)] = null);

(statearr_34720_34754[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (18))){
var inst_34653 = (state_34683[(2)]);
var state_34683__$1 = state_34683;
var statearr_34721_34755 = state_34683__$1;
(statearr_34721_34755[(2)] = inst_34653);

(statearr_34721_34755[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34684 === (8))){
var inst_34627 = (state_34683[(14)]);
var inst_34626 = (state_34683[(15)]);
var inst_34629 = (inst_34627 < inst_34626);
var inst_34630 = inst_34629;
var state_34683__$1 = state_34683;
if(cljs.core.truth_(inst_34630)){
var statearr_34722_34756 = state_34683__$1;
(statearr_34722_34756[(1)] = (10));

} else {
var statearr_34723_34757 = state_34683__$1;
(statearr_34723_34757[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___34729,mults,ensure_mult,p))
;
return ((function (switch__33350__auto__,c__33495__auto___34729,mults,ensure_mult,p){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_34724 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34724[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_34724[(1)] = (1));

return statearr_34724;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_34683){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34683);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34725){if((e34725 instanceof Object)){
var ex__33354__auto__ = e34725;
var statearr_34726_34758 = state_34683;
(statearr_34726_34758[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34683);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34725;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34759 = state_34683;
state_34683 = G__34759;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_34683){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_34683);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___34729,mults,ensure_mult,p))
})();
var state__33497__auto__ = (function (){var statearr_34727 = f__33496__auto__.call(null);
(statearr_34727[(6)] = c__33495__auto___34729);

return statearr_34727;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___34729,mults,ensure_mult,p))
);


return p;
});

cljs.core.async.pub.cljs$lang$maxFixedArity = 3;

/**
 * Subscribes a channel to a topic of a pub.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.sub = (function cljs$core$async$sub(var_args){
var G__34761 = arguments.length;
switch (G__34761) {
case 3:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3 = (function (p,topic,ch){
return cljs.core.async.sub.call(null,p,topic,ch,true);
});

cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4 = (function (p,topic,ch,close_QMARK_){
return cljs.core.async.sub_STAR_.call(null,p,topic,ch,close_QMARK_);
});

cljs.core.async.sub.cljs$lang$maxFixedArity = 4;

/**
 * Unsubscribes a channel from a topic of a pub
 */
cljs.core.async.unsub = (function cljs$core$async$unsub(p,topic,ch){
return cljs.core.async.unsub_STAR_.call(null,p,topic,ch);
});
/**
 * Unsubscribes all channels from a pub, or a topic of a pub
 */
cljs.core.async.unsub_all = (function cljs$core$async$unsub_all(var_args){
var G__34764 = arguments.length;
switch (G__34764) {
case 1:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1 = (function (p){
return cljs.core.async.unsub_all_STAR_.call(null,p);
});

cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2 = (function (p,topic){
return cljs.core.async.unsub_all_STAR_.call(null,p,topic);
});

cljs.core.async.unsub_all.cljs$lang$maxFixedArity = 2;

/**
 * Takes a function and a collection of source channels, and returns a
 *   channel which contains the values produced by applying f to the set
 *   of first items taken from each source channel, followed by applying
 *   f to the set of second items from each channel, until any one of the
 *   channels is closed, at which point the output channel will be
 *   closed. The returned channel will be unbuffered by default, or a
 *   buf-or-n can be supplied
 */
cljs.core.async.map = (function cljs$core$async$map(var_args){
var G__34767 = arguments.length;
switch (G__34767) {
case 2:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.map.cljs$core$IFn$_invoke$arity$2 = (function (f,chs){
return cljs.core.async.map.call(null,f,chs,null);
});

cljs.core.async.map.cljs$core$IFn$_invoke$arity$3 = (function (f,chs,buf_or_n){
var chs__$1 = cljs.core.vec.call(null,chs);
var out = cljs.core.async.chan.call(null,buf_or_n);
var cnt = cljs.core.count.call(null,chs__$1);
var rets = cljs.core.object_array.call(null,cnt);
var dchan = cljs.core.async.chan.call(null,(1));
var dctr = cljs.core.atom.call(null,null);
var done = cljs.core.mapv.call(null,((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (i){
return ((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (ret){
(rets[i] = ret);

if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.call(null,dchan,rets.slice((0)));
} else {
return null;
}
});
;})(chs__$1,out,cnt,rets,dchan,dctr))
});})(chs__$1,out,cnt,rets,dchan,dctr))
,cljs.core.range.call(null,cnt));
var c__33495__auto___34834 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___34834,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___34834,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (state_34806){
var state_val_34807 = (state_34806[(1)]);
if((state_val_34807 === (7))){
var state_34806__$1 = state_34806;
var statearr_34808_34835 = state_34806__$1;
(statearr_34808_34835[(2)] = null);

(statearr_34808_34835[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (1))){
var state_34806__$1 = state_34806;
var statearr_34809_34836 = state_34806__$1;
(statearr_34809_34836[(2)] = null);

(statearr_34809_34836[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (4))){
var inst_34770 = (state_34806[(7)]);
var inst_34772 = (inst_34770 < cnt);
var state_34806__$1 = state_34806;
if(cljs.core.truth_(inst_34772)){
var statearr_34810_34837 = state_34806__$1;
(statearr_34810_34837[(1)] = (6));

} else {
var statearr_34811_34838 = state_34806__$1;
(statearr_34811_34838[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (15))){
var inst_34802 = (state_34806[(2)]);
var state_34806__$1 = state_34806;
var statearr_34812_34839 = state_34806__$1;
(statearr_34812_34839[(2)] = inst_34802);

(statearr_34812_34839[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (13))){
var inst_34795 = cljs.core.async.close_BANG_.call(null,out);
var state_34806__$1 = state_34806;
var statearr_34813_34840 = state_34806__$1;
(statearr_34813_34840[(2)] = inst_34795);

(statearr_34813_34840[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (6))){
var state_34806__$1 = state_34806;
var statearr_34814_34841 = state_34806__$1;
(statearr_34814_34841[(2)] = null);

(statearr_34814_34841[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (3))){
var inst_34804 = (state_34806[(2)]);
var state_34806__$1 = state_34806;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34806__$1,inst_34804);
} else {
if((state_val_34807 === (12))){
var inst_34792 = (state_34806[(8)]);
var inst_34792__$1 = (state_34806[(2)]);
var inst_34793 = cljs.core.some.call(null,cljs.core.nil_QMARK_,inst_34792__$1);
var state_34806__$1 = (function (){var statearr_34815 = state_34806;
(statearr_34815[(8)] = inst_34792__$1);

return statearr_34815;
})();
if(cljs.core.truth_(inst_34793)){
var statearr_34816_34842 = state_34806__$1;
(statearr_34816_34842[(1)] = (13));

} else {
var statearr_34817_34843 = state_34806__$1;
(statearr_34817_34843[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (2))){
var inst_34769 = cljs.core.reset_BANG_.call(null,dctr,cnt);
var inst_34770 = (0);
var state_34806__$1 = (function (){var statearr_34818 = state_34806;
(statearr_34818[(9)] = inst_34769);

(statearr_34818[(7)] = inst_34770);

return statearr_34818;
})();
var statearr_34819_34844 = state_34806__$1;
(statearr_34819_34844[(2)] = null);

(statearr_34819_34844[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (11))){
var inst_34770 = (state_34806[(7)]);
var _ = cljs.core.async.impl.ioc_helpers.add_exception_frame.call(null,state_34806,(10),Object,null,(9));
var inst_34779 = chs__$1.call(null,inst_34770);
var inst_34780 = done.call(null,inst_34770);
var inst_34781 = cljs.core.async.take_BANG_.call(null,inst_34779,inst_34780);
var state_34806__$1 = state_34806;
var statearr_34820_34845 = state_34806__$1;
(statearr_34820_34845[(2)] = inst_34781);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34806__$1);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (9))){
var inst_34770 = (state_34806[(7)]);
var inst_34783 = (state_34806[(2)]);
var inst_34784 = (inst_34770 + (1));
var inst_34770__$1 = inst_34784;
var state_34806__$1 = (function (){var statearr_34821 = state_34806;
(statearr_34821[(7)] = inst_34770__$1);

(statearr_34821[(10)] = inst_34783);

return statearr_34821;
})();
var statearr_34822_34846 = state_34806__$1;
(statearr_34822_34846[(2)] = null);

(statearr_34822_34846[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (5))){
var inst_34790 = (state_34806[(2)]);
var state_34806__$1 = (function (){var statearr_34823 = state_34806;
(statearr_34823[(11)] = inst_34790);

return statearr_34823;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_34806__$1,(12),dchan);
} else {
if((state_val_34807 === (14))){
var inst_34792 = (state_34806[(8)]);
var inst_34797 = cljs.core.apply.call(null,f,inst_34792);
var state_34806__$1 = state_34806;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_34806__$1,(16),out,inst_34797);
} else {
if((state_val_34807 === (16))){
var inst_34799 = (state_34806[(2)]);
var state_34806__$1 = (function (){var statearr_34824 = state_34806;
(statearr_34824[(12)] = inst_34799);

return statearr_34824;
})();
var statearr_34825_34847 = state_34806__$1;
(statearr_34825_34847[(2)] = null);

(statearr_34825_34847[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (10))){
var inst_34774 = (state_34806[(2)]);
var inst_34775 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);
var state_34806__$1 = (function (){var statearr_34826 = state_34806;
(statearr_34826[(13)] = inst_34774);

return statearr_34826;
})();
var statearr_34827_34848 = state_34806__$1;
(statearr_34827_34848[(2)] = inst_34775);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34806__$1);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34807 === (8))){
var inst_34788 = (state_34806[(2)]);
var state_34806__$1 = state_34806;
var statearr_34828_34849 = state_34806__$1;
(statearr_34828_34849[(2)] = inst_34788);

(statearr_34828_34849[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___34834,chs__$1,out,cnt,rets,dchan,dctr,done))
;
return ((function (switch__33350__auto__,c__33495__auto___34834,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_34829 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34829[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_34829[(1)] = (1));

return statearr_34829;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_34806){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34806);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34830){if((e34830 instanceof Object)){
var ex__33354__auto__ = e34830;
var statearr_34831_34850 = state_34806;
(statearr_34831_34850[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34806);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34830;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34851 = state_34806;
state_34806 = G__34851;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_34806){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_34806);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___34834,chs__$1,out,cnt,rets,dchan,dctr,done))
})();
var state__33497__auto__ = (function (){var statearr_34832 = f__33496__auto__.call(null);
(statearr_34832[(6)] = c__33495__auto___34834);

return statearr_34832;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___34834,chs__$1,out,cnt,rets,dchan,dctr,done))
);


return out;
});

cljs.core.async.map.cljs$lang$maxFixedArity = 3;

/**
 * Takes a collection of source channels and returns a channel which
 *   contains all values taken from them. The returned channel will be
 *   unbuffered by default, or a buf-or-n can be supplied. The channel
 *   will close after all the source channels have closed.
 */
cljs.core.async.merge = (function cljs$core$async$merge(var_args){
var G__34854 = arguments.length;
switch (G__34854) {
case 1:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1 = (function (chs){
return cljs.core.async.merge.call(null,chs,null);
});

cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2 = (function (chs,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__33495__auto___34908 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___34908,out){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___34908,out){
return (function (state_34886){
var state_val_34887 = (state_34886[(1)]);
if((state_val_34887 === (7))){
var inst_34866 = (state_34886[(7)]);
var inst_34865 = (state_34886[(8)]);
var inst_34865__$1 = (state_34886[(2)]);
var inst_34866__$1 = cljs.core.nth.call(null,inst_34865__$1,(0),null);
var inst_34867 = cljs.core.nth.call(null,inst_34865__$1,(1),null);
var inst_34868 = (inst_34866__$1 == null);
var state_34886__$1 = (function (){var statearr_34888 = state_34886;
(statearr_34888[(7)] = inst_34866__$1);

(statearr_34888[(8)] = inst_34865__$1);

(statearr_34888[(9)] = inst_34867);

return statearr_34888;
})();
if(cljs.core.truth_(inst_34868)){
var statearr_34889_34909 = state_34886__$1;
(statearr_34889_34909[(1)] = (8));

} else {
var statearr_34890_34910 = state_34886__$1;
(statearr_34890_34910[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34887 === (1))){
var inst_34855 = cljs.core.vec.call(null,chs);
var inst_34856 = inst_34855;
var state_34886__$1 = (function (){var statearr_34891 = state_34886;
(statearr_34891[(10)] = inst_34856);

return statearr_34891;
})();
var statearr_34892_34911 = state_34886__$1;
(statearr_34892_34911[(2)] = null);

(statearr_34892_34911[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34887 === (4))){
var inst_34856 = (state_34886[(10)]);
var state_34886__$1 = state_34886;
return cljs.core.async.ioc_alts_BANG_.call(null,state_34886__$1,(7),inst_34856);
} else {
if((state_val_34887 === (6))){
var inst_34882 = (state_34886[(2)]);
var state_34886__$1 = state_34886;
var statearr_34893_34912 = state_34886__$1;
(statearr_34893_34912[(2)] = inst_34882);

(statearr_34893_34912[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34887 === (3))){
var inst_34884 = (state_34886[(2)]);
var state_34886__$1 = state_34886;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34886__$1,inst_34884);
} else {
if((state_val_34887 === (2))){
var inst_34856 = (state_34886[(10)]);
var inst_34858 = cljs.core.count.call(null,inst_34856);
var inst_34859 = (inst_34858 > (0));
var state_34886__$1 = state_34886;
if(cljs.core.truth_(inst_34859)){
var statearr_34895_34913 = state_34886__$1;
(statearr_34895_34913[(1)] = (4));

} else {
var statearr_34896_34914 = state_34886__$1;
(statearr_34896_34914[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34887 === (11))){
var inst_34856 = (state_34886[(10)]);
var inst_34875 = (state_34886[(2)]);
var tmp34894 = inst_34856;
var inst_34856__$1 = tmp34894;
var state_34886__$1 = (function (){var statearr_34897 = state_34886;
(statearr_34897[(10)] = inst_34856__$1);

(statearr_34897[(11)] = inst_34875);

return statearr_34897;
})();
var statearr_34898_34915 = state_34886__$1;
(statearr_34898_34915[(2)] = null);

(statearr_34898_34915[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34887 === (9))){
var inst_34866 = (state_34886[(7)]);
var state_34886__$1 = state_34886;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_34886__$1,(11),out,inst_34866);
} else {
if((state_val_34887 === (5))){
var inst_34880 = cljs.core.async.close_BANG_.call(null,out);
var state_34886__$1 = state_34886;
var statearr_34899_34916 = state_34886__$1;
(statearr_34899_34916[(2)] = inst_34880);

(statearr_34899_34916[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34887 === (10))){
var inst_34878 = (state_34886[(2)]);
var state_34886__$1 = state_34886;
var statearr_34900_34917 = state_34886__$1;
(statearr_34900_34917[(2)] = inst_34878);

(statearr_34900_34917[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34887 === (8))){
var inst_34856 = (state_34886[(10)]);
var inst_34866 = (state_34886[(7)]);
var inst_34865 = (state_34886[(8)]);
var inst_34867 = (state_34886[(9)]);
var inst_34870 = (function (){var cs = inst_34856;
var vec__34861 = inst_34865;
var v = inst_34866;
var c = inst_34867;
return ((function (cs,vec__34861,v,c,inst_34856,inst_34866,inst_34865,inst_34867,state_val_34887,c__33495__auto___34908,out){
return (function (p1__34852_SHARP_){
return cljs.core.not_EQ_.call(null,c,p1__34852_SHARP_);
});
;})(cs,vec__34861,v,c,inst_34856,inst_34866,inst_34865,inst_34867,state_val_34887,c__33495__auto___34908,out))
})();
var inst_34871 = cljs.core.filterv.call(null,inst_34870,inst_34856);
var inst_34856__$1 = inst_34871;
var state_34886__$1 = (function (){var statearr_34901 = state_34886;
(statearr_34901[(10)] = inst_34856__$1);

return statearr_34901;
})();
var statearr_34902_34918 = state_34886__$1;
(statearr_34902_34918[(2)] = null);

(statearr_34902_34918[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___34908,out))
;
return ((function (switch__33350__auto__,c__33495__auto___34908,out){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_34903 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_34903[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_34903[(1)] = (1));

return statearr_34903;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_34886){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34886);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34904){if((e34904 instanceof Object)){
var ex__33354__auto__ = e34904;
var statearr_34905_34919 = state_34886;
(statearr_34905_34919[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34886);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34904;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34920 = state_34886;
state_34886 = G__34920;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_34886){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_34886);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___34908,out))
})();
var state__33497__auto__ = (function (){var statearr_34906 = f__33496__auto__.call(null);
(statearr_34906[(6)] = c__33495__auto___34908);

return statearr_34906;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___34908,out))
);


return out;
});

cljs.core.async.merge.cljs$lang$maxFixedArity = 2;

/**
 * Returns a channel containing the single (collection) result of the
 *   items taken from the channel conjoined to the supplied
 *   collection. ch must close before into produces a result.
 */
cljs.core.async.into = (function cljs$core$async$into(coll,ch){
return cljs.core.async.reduce.call(null,cljs.core.conj,coll,ch);
});
/**
 * Returns a channel that will return, at most, n items from ch. After n items
 * have been returned, or ch has been closed, the return chanel will close.
 * 
 *   The output channel is unbuffered by default, unless buf-or-n is given.
 */
cljs.core.async.take = (function cljs$core$async$take(var_args){
var G__34922 = arguments.length;
switch (G__34922) {
case 2:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.take.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.take.call(null,n,ch,null);
});

cljs.core.async.take.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__33495__auto___34967 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___34967,out){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___34967,out){
return (function (state_34946){
var state_val_34947 = (state_34946[(1)]);
if((state_val_34947 === (7))){
var inst_34928 = (state_34946[(7)]);
var inst_34928__$1 = (state_34946[(2)]);
var inst_34929 = (inst_34928__$1 == null);
var inst_34930 = cljs.core.not.call(null,inst_34929);
var state_34946__$1 = (function (){var statearr_34948 = state_34946;
(statearr_34948[(7)] = inst_34928__$1);

return statearr_34948;
})();
if(inst_34930){
var statearr_34949_34968 = state_34946__$1;
(statearr_34949_34968[(1)] = (8));

} else {
var statearr_34950_34969 = state_34946__$1;
(statearr_34950_34969[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (1))){
var inst_34923 = (0);
var state_34946__$1 = (function (){var statearr_34951 = state_34946;
(statearr_34951[(8)] = inst_34923);

return statearr_34951;
})();
var statearr_34952_34970 = state_34946__$1;
(statearr_34952_34970[(2)] = null);

(statearr_34952_34970[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (4))){
var state_34946__$1 = state_34946;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_34946__$1,(7),ch);
} else {
if((state_val_34947 === (6))){
var inst_34941 = (state_34946[(2)]);
var state_34946__$1 = state_34946;
var statearr_34953_34971 = state_34946__$1;
(statearr_34953_34971[(2)] = inst_34941);

(statearr_34953_34971[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (3))){
var inst_34943 = (state_34946[(2)]);
var inst_34944 = cljs.core.async.close_BANG_.call(null,out);
var state_34946__$1 = (function (){var statearr_34954 = state_34946;
(statearr_34954[(9)] = inst_34943);

return statearr_34954;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_34946__$1,inst_34944);
} else {
if((state_val_34947 === (2))){
var inst_34923 = (state_34946[(8)]);
var inst_34925 = (inst_34923 < n);
var state_34946__$1 = state_34946;
if(cljs.core.truth_(inst_34925)){
var statearr_34955_34972 = state_34946__$1;
(statearr_34955_34972[(1)] = (4));

} else {
var statearr_34956_34973 = state_34946__$1;
(statearr_34956_34973[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (11))){
var inst_34923 = (state_34946[(8)]);
var inst_34933 = (state_34946[(2)]);
var inst_34934 = (inst_34923 + (1));
var inst_34923__$1 = inst_34934;
var state_34946__$1 = (function (){var statearr_34957 = state_34946;
(statearr_34957[(8)] = inst_34923__$1);

(statearr_34957[(10)] = inst_34933);

return statearr_34957;
})();
var statearr_34958_34974 = state_34946__$1;
(statearr_34958_34974[(2)] = null);

(statearr_34958_34974[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (9))){
var state_34946__$1 = state_34946;
var statearr_34959_34975 = state_34946__$1;
(statearr_34959_34975[(2)] = null);

(statearr_34959_34975[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (5))){
var state_34946__$1 = state_34946;
var statearr_34960_34976 = state_34946__$1;
(statearr_34960_34976[(2)] = null);

(statearr_34960_34976[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (10))){
var inst_34938 = (state_34946[(2)]);
var state_34946__$1 = state_34946;
var statearr_34961_34977 = state_34946__$1;
(statearr_34961_34977[(2)] = inst_34938);

(statearr_34961_34977[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_34947 === (8))){
var inst_34928 = (state_34946[(7)]);
var state_34946__$1 = state_34946;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_34946__$1,(11),out,inst_34928);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___34967,out))
;
return ((function (switch__33350__auto__,c__33495__auto___34967,out){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_34962 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_34962[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_34962[(1)] = (1));

return statearr_34962;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_34946){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_34946);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e34963){if((e34963 instanceof Object)){
var ex__33354__auto__ = e34963;
var statearr_34964_34978 = state_34946;
(statearr_34964_34978[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_34946);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e34963;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__34979 = state_34946;
state_34946 = G__34979;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_34946){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_34946);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___34967,out))
})();
var state__33497__auto__ = (function (){var statearr_34965 = f__33496__auto__.call(null);
(statearr_34965[(6)] = c__33495__auto___34967);

return statearr_34965;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___34967,out))
);


return out;
});

cljs.core.async.take.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_LT_ = (function cljs$core$async$map_LT_(f,ch){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async34981 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34981 = (function (f,ch,meta34982){
this.f = f;
this.ch = ch;
this.meta34982 = meta34982;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_34983,meta34982__$1){
var self__ = this;
var _34983__$1 = this;
return (new cljs.core.async.t_cljs$core$async34981(self__.f,self__.ch,meta34982__$1));
});

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_34983){
var self__ = this;
var _34983__$1 = this;
return self__.meta34982;
});

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,(function (){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async34984 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34984 = (function (f,ch,meta34982,_,fn1,meta34985){
this.f = f;
this.ch = ch;
this.meta34982 = meta34982;
this._ = _;
this.fn1 = fn1;
this.meta34985 = meta34985;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async34984.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (___$1){
return (function (_34986,meta34985__$1){
var self__ = this;
var _34986__$1 = this;
return (new cljs.core.async.t_cljs$core$async34984(self__.f,self__.ch,self__.meta34982,self__._,self__.fn1,meta34985__$1));
});})(___$1))
;

cljs.core.async.t_cljs$core$async34984.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (___$1){
return (function (_34986){
var self__ = this;
var _34986__$1 = this;
return self__.meta34985;
});})(___$1))
;

cljs.core.async.t_cljs$core$async34984.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34984.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.fn1);
});})(___$1))
;

cljs.core.async.t_cljs$core$async34984.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
return true;
});})(___$1))
;

cljs.core.async.t_cljs$core$async34984.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
var f1 = cljs.core.async.impl.protocols.commit.call(null,self__.fn1);
return ((function (f1,___$2,___$1){
return (function (p1__34980_SHARP_){
return f1.call(null,(((p1__34980_SHARP_ == null))?null:self__.f.call(null,p1__34980_SHARP_)));
});
;})(f1,___$2,___$1))
});})(___$1))
;

cljs.core.async.t_cljs$core$async34984.getBasis = ((function (___$1){
return (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta34982","meta34982",-94845871,null),cljs.core.with_meta(new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core.async","t_cljs$core$async34981","cljs.core.async/t_cljs$core$async34981",354456384,null)], null)),new cljs.core.Symbol(null,"fn1","fn1",895834444,null),new cljs.core.Symbol(null,"meta34985","meta34985",-513411510,null)], null);
});})(___$1))
;

cljs.core.async.t_cljs$core$async34984.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async34984.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34984";

cljs.core.async.t_cljs$core$async34984.cljs$lang$ctorPrWriter = ((function (___$1){
return (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async34984");
});})(___$1))
;

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34984.
 */
cljs.core.async.__GT_t_cljs$core$async34984 = ((function (___$1){
return (function cljs$core$async$map_LT__$___GT_t_cljs$core$async34984(f__$1,ch__$1,meta34982__$1,___$2,fn1__$1,meta34985){
return (new cljs.core.async.t_cljs$core$async34984(f__$1,ch__$1,meta34982__$1,___$2,fn1__$1,meta34985));
});})(___$1))
;

}

return (new cljs.core.async.t_cljs$core$async34984(self__.f,self__.ch,self__.meta34982,___$1,fn1,cljs.core.PersistentArrayMap.EMPTY));
})()
);
if(cljs.core.truth_((function (){var and__4120__auto__ = ret;
if(cljs.core.truth_(and__4120__auto__)){
return (!((cljs.core.deref.call(null,ret) == null)));
} else {
return and__4120__auto__;
}
})())){
return cljs.core.async.impl.channels.box.call(null,self__.f.call(null,cljs.core.deref.call(null,ret)));
} else {
return ret;
}
});

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34981.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
});

cljs.core.async.t_cljs$core$async34981.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta34982","meta34982",-94845871,null)], null);
});

cljs.core.async.t_cljs$core$async34981.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async34981.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34981";

cljs.core.async.t_cljs$core$async34981.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async34981");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34981.
 */
cljs.core.async.__GT_t_cljs$core$async34981 = (function cljs$core$async$map_LT__$___GT_t_cljs$core$async34981(f__$1,ch__$1,meta34982){
return (new cljs.core.async.t_cljs$core$async34981(f__$1,ch__$1,meta34982));
});

}

return (new cljs.core.async.t_cljs$core$async34981(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_GT_ = (function cljs$core$async$map_GT_(f,ch){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async34987 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34987 = (function (f,ch,meta34988){
this.f = f;
this.ch = ch;
this.meta34988 = meta34988;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_34989,meta34988__$1){
var self__ = this;
var _34989__$1 = this;
return (new cljs.core.async.t_cljs$core$async34987(self__.f,self__.ch,meta34988__$1));
});

cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_34989){
var self__ = this;
var _34989__$1 = this;
return self__.meta34988;
});

cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});

cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34987.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,self__.f.call(null,val),fn1);
});

cljs.core.async.t_cljs$core$async34987.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta34988","meta34988",304504678,null)], null);
});

cljs.core.async.t_cljs$core$async34987.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async34987.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34987";

cljs.core.async.t_cljs$core$async34987.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async34987");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34987.
 */
cljs.core.async.__GT_t_cljs$core$async34987 = (function cljs$core$async$map_GT__$___GT_t_cljs$core$async34987(f__$1,ch__$1,meta34988){
return (new cljs.core.async.t_cljs$core$async34987(f__$1,ch__$1,meta34988));
});

}

return (new cljs.core.async.t_cljs$core$async34987(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_GT_ = (function cljs$core$async$filter_GT_(p,ch){
if((typeof cljs !== 'undefined') && (typeof cljs.core !== 'undefined') && (typeof cljs.core.async !== 'undefined') && (typeof cljs.core.async.t_cljs$core$async34990 !== 'undefined')){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async34990 = (function (p,ch,meta34991){
this.p = p;
this.ch = ch;
this.meta34991 = meta34991;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_34992,meta34991__$1){
var self__ = this;
var _34992__$1 = this;
return (new cljs.core.async.t_cljs$core$async34990(self__.p,self__.ch,meta34991__$1));
});

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_34992){
var self__ = this;
var _34992__$1 = this;
return self__.meta34991;
});

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async34990.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_(self__.p.call(null,val))){
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
} else {
return cljs.core.async.impl.channels.box.call(null,cljs.core.not.call(null,cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch)));
}
});

cljs.core.async.t_cljs$core$async34990.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"p","p",1791580836,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta34991","meta34991",-1205506667,null)], null);
});

cljs.core.async.t_cljs$core$async34990.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async34990.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async34990";

cljs.core.async.t_cljs$core$async34990.cljs$lang$ctorPrWriter = (function (this__4374__auto__,writer__4375__auto__,opt__4376__auto__){
return cljs.core._write.call(null,writer__4375__auto__,"cljs.core.async/t_cljs$core$async34990");
});

/**
 * Positional factory function for cljs.core.async/t_cljs$core$async34990.
 */
cljs.core.async.__GT_t_cljs$core$async34990 = (function cljs$core$async$filter_GT__$___GT_t_cljs$core$async34990(p__$1,ch__$1,meta34991){
return (new cljs.core.async.t_cljs$core$async34990(p__$1,ch__$1,meta34991));
});

}

return (new cljs.core.async.t_cljs$core$async34990(p,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_GT_ = (function cljs$core$async$remove_GT_(p,ch){
return cljs.core.async.filter_GT_.call(null,cljs.core.complement.call(null,p),ch);
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_LT_ = (function cljs$core$async$filter_LT_(var_args){
var G__34994 = arguments.length;
switch (G__34994) {
case 2:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.filter_LT_.call(null,p,ch,null);
});

cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__33495__auto___35034 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___35034,out){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___35034,out){
return (function (state_35015){
var state_val_35016 = (state_35015[(1)]);
if((state_val_35016 === (7))){
var inst_35011 = (state_35015[(2)]);
var state_35015__$1 = state_35015;
var statearr_35017_35035 = state_35015__$1;
(statearr_35017_35035[(2)] = inst_35011);

(statearr_35017_35035[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (1))){
var state_35015__$1 = state_35015;
var statearr_35018_35036 = state_35015__$1;
(statearr_35018_35036[(2)] = null);

(statearr_35018_35036[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (4))){
var inst_34997 = (state_35015[(7)]);
var inst_34997__$1 = (state_35015[(2)]);
var inst_34998 = (inst_34997__$1 == null);
var state_35015__$1 = (function (){var statearr_35019 = state_35015;
(statearr_35019[(7)] = inst_34997__$1);

return statearr_35019;
})();
if(cljs.core.truth_(inst_34998)){
var statearr_35020_35037 = state_35015__$1;
(statearr_35020_35037[(1)] = (5));

} else {
var statearr_35021_35038 = state_35015__$1;
(statearr_35021_35038[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (6))){
var inst_34997 = (state_35015[(7)]);
var inst_35002 = p.call(null,inst_34997);
var state_35015__$1 = state_35015;
if(cljs.core.truth_(inst_35002)){
var statearr_35022_35039 = state_35015__$1;
(statearr_35022_35039[(1)] = (8));

} else {
var statearr_35023_35040 = state_35015__$1;
(statearr_35023_35040[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (3))){
var inst_35013 = (state_35015[(2)]);
var state_35015__$1 = state_35015;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_35015__$1,inst_35013);
} else {
if((state_val_35016 === (2))){
var state_35015__$1 = state_35015;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35015__$1,(4),ch);
} else {
if((state_val_35016 === (11))){
var inst_35005 = (state_35015[(2)]);
var state_35015__$1 = state_35015;
var statearr_35024_35041 = state_35015__$1;
(statearr_35024_35041[(2)] = inst_35005);

(statearr_35024_35041[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (9))){
var state_35015__$1 = state_35015;
var statearr_35025_35042 = state_35015__$1;
(statearr_35025_35042[(2)] = null);

(statearr_35025_35042[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (5))){
var inst_35000 = cljs.core.async.close_BANG_.call(null,out);
var state_35015__$1 = state_35015;
var statearr_35026_35043 = state_35015__$1;
(statearr_35026_35043[(2)] = inst_35000);

(statearr_35026_35043[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (10))){
var inst_35008 = (state_35015[(2)]);
var state_35015__$1 = (function (){var statearr_35027 = state_35015;
(statearr_35027[(8)] = inst_35008);

return statearr_35027;
})();
var statearr_35028_35044 = state_35015__$1;
(statearr_35028_35044[(2)] = null);

(statearr_35028_35044[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35016 === (8))){
var inst_34997 = (state_35015[(7)]);
var state_35015__$1 = state_35015;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35015__$1,(11),out,inst_34997);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___35034,out))
;
return ((function (switch__33350__auto__,c__33495__auto___35034,out){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_35029 = [null,null,null,null,null,null,null,null,null];
(statearr_35029[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_35029[(1)] = (1));

return statearr_35029;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_35015){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_35015);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e35030){if((e35030 instanceof Object)){
var ex__33354__auto__ = e35030;
var statearr_35031_35045 = state_35015;
(statearr_35031_35045[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_35015);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e35030;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35046 = state_35015;
state_35015 = G__35046;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_35015){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_35015);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___35034,out))
})();
var state__33497__auto__ = (function (){var statearr_35032 = f__33496__auto__.call(null);
(statearr_35032[(6)] = c__33495__auto___35034);

return statearr_35032;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___35034,out))
);


return out;
});

cljs.core.async.filter_LT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_LT_ = (function cljs$core$async$remove_LT_(var_args){
var G__35048 = arguments.length;
switch (G__35048) {
case 2:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.remove_LT_.call(null,p,ch,null);
});

cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
return cljs.core.async.filter_LT_.call(null,cljs.core.complement.call(null,p),ch,buf_or_n);
});

cljs.core.async.remove_LT_.cljs$lang$maxFixedArity = 3;

cljs.core.async.mapcat_STAR_ = (function cljs$core$async$mapcat_STAR_(f,in$,out){
var c__33495__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto__){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto__){
return (function (state_35111){
var state_val_35112 = (state_35111[(1)]);
if((state_val_35112 === (7))){
var inst_35107 = (state_35111[(2)]);
var state_35111__$1 = state_35111;
var statearr_35113_35151 = state_35111__$1;
(statearr_35113_35151[(2)] = inst_35107);

(statearr_35113_35151[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (20))){
var inst_35077 = (state_35111[(7)]);
var inst_35088 = (state_35111[(2)]);
var inst_35089 = cljs.core.next.call(null,inst_35077);
var inst_35063 = inst_35089;
var inst_35064 = null;
var inst_35065 = (0);
var inst_35066 = (0);
var state_35111__$1 = (function (){var statearr_35114 = state_35111;
(statearr_35114[(8)] = inst_35063);

(statearr_35114[(9)] = inst_35064);

(statearr_35114[(10)] = inst_35065);

(statearr_35114[(11)] = inst_35088);

(statearr_35114[(12)] = inst_35066);

return statearr_35114;
})();
var statearr_35115_35152 = state_35111__$1;
(statearr_35115_35152[(2)] = null);

(statearr_35115_35152[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (1))){
var state_35111__$1 = state_35111;
var statearr_35116_35153 = state_35111__$1;
(statearr_35116_35153[(2)] = null);

(statearr_35116_35153[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (4))){
var inst_35052 = (state_35111[(13)]);
var inst_35052__$1 = (state_35111[(2)]);
var inst_35053 = (inst_35052__$1 == null);
var state_35111__$1 = (function (){var statearr_35117 = state_35111;
(statearr_35117[(13)] = inst_35052__$1);

return statearr_35117;
})();
if(cljs.core.truth_(inst_35053)){
var statearr_35118_35154 = state_35111__$1;
(statearr_35118_35154[(1)] = (5));

} else {
var statearr_35119_35155 = state_35111__$1;
(statearr_35119_35155[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (15))){
var state_35111__$1 = state_35111;
var statearr_35123_35156 = state_35111__$1;
(statearr_35123_35156[(2)] = null);

(statearr_35123_35156[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (21))){
var state_35111__$1 = state_35111;
var statearr_35124_35157 = state_35111__$1;
(statearr_35124_35157[(2)] = null);

(statearr_35124_35157[(1)] = (23));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (13))){
var inst_35063 = (state_35111[(8)]);
var inst_35064 = (state_35111[(9)]);
var inst_35065 = (state_35111[(10)]);
var inst_35066 = (state_35111[(12)]);
var inst_35073 = (state_35111[(2)]);
var inst_35074 = (inst_35066 + (1));
var tmp35120 = inst_35063;
var tmp35121 = inst_35064;
var tmp35122 = inst_35065;
var inst_35063__$1 = tmp35120;
var inst_35064__$1 = tmp35121;
var inst_35065__$1 = tmp35122;
var inst_35066__$1 = inst_35074;
var state_35111__$1 = (function (){var statearr_35125 = state_35111;
(statearr_35125[(8)] = inst_35063__$1);

(statearr_35125[(9)] = inst_35064__$1);

(statearr_35125[(14)] = inst_35073);

(statearr_35125[(10)] = inst_35065__$1);

(statearr_35125[(12)] = inst_35066__$1);

return statearr_35125;
})();
var statearr_35126_35158 = state_35111__$1;
(statearr_35126_35158[(2)] = null);

(statearr_35126_35158[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (22))){
var state_35111__$1 = state_35111;
var statearr_35127_35159 = state_35111__$1;
(statearr_35127_35159[(2)] = null);

(statearr_35127_35159[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (6))){
var inst_35052 = (state_35111[(13)]);
var inst_35061 = f.call(null,inst_35052);
var inst_35062 = cljs.core.seq.call(null,inst_35061);
var inst_35063 = inst_35062;
var inst_35064 = null;
var inst_35065 = (0);
var inst_35066 = (0);
var state_35111__$1 = (function (){var statearr_35128 = state_35111;
(statearr_35128[(8)] = inst_35063);

(statearr_35128[(9)] = inst_35064);

(statearr_35128[(10)] = inst_35065);

(statearr_35128[(12)] = inst_35066);

return statearr_35128;
})();
var statearr_35129_35160 = state_35111__$1;
(statearr_35129_35160[(2)] = null);

(statearr_35129_35160[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (17))){
var inst_35077 = (state_35111[(7)]);
var inst_35081 = cljs.core.chunk_first.call(null,inst_35077);
var inst_35082 = cljs.core.chunk_rest.call(null,inst_35077);
var inst_35083 = cljs.core.count.call(null,inst_35081);
var inst_35063 = inst_35082;
var inst_35064 = inst_35081;
var inst_35065 = inst_35083;
var inst_35066 = (0);
var state_35111__$1 = (function (){var statearr_35130 = state_35111;
(statearr_35130[(8)] = inst_35063);

(statearr_35130[(9)] = inst_35064);

(statearr_35130[(10)] = inst_35065);

(statearr_35130[(12)] = inst_35066);

return statearr_35130;
})();
var statearr_35131_35161 = state_35111__$1;
(statearr_35131_35161[(2)] = null);

(statearr_35131_35161[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (3))){
var inst_35109 = (state_35111[(2)]);
var state_35111__$1 = state_35111;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_35111__$1,inst_35109);
} else {
if((state_val_35112 === (12))){
var inst_35097 = (state_35111[(2)]);
var state_35111__$1 = state_35111;
var statearr_35132_35162 = state_35111__$1;
(statearr_35132_35162[(2)] = inst_35097);

(statearr_35132_35162[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (2))){
var state_35111__$1 = state_35111;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35111__$1,(4),in$);
} else {
if((state_val_35112 === (23))){
var inst_35105 = (state_35111[(2)]);
var state_35111__$1 = state_35111;
var statearr_35133_35163 = state_35111__$1;
(statearr_35133_35163[(2)] = inst_35105);

(statearr_35133_35163[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (19))){
var inst_35092 = (state_35111[(2)]);
var state_35111__$1 = state_35111;
var statearr_35134_35164 = state_35111__$1;
(statearr_35134_35164[(2)] = inst_35092);

(statearr_35134_35164[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (11))){
var inst_35063 = (state_35111[(8)]);
var inst_35077 = (state_35111[(7)]);
var inst_35077__$1 = cljs.core.seq.call(null,inst_35063);
var state_35111__$1 = (function (){var statearr_35135 = state_35111;
(statearr_35135[(7)] = inst_35077__$1);

return statearr_35135;
})();
if(inst_35077__$1){
var statearr_35136_35165 = state_35111__$1;
(statearr_35136_35165[(1)] = (14));

} else {
var statearr_35137_35166 = state_35111__$1;
(statearr_35137_35166[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (9))){
var inst_35099 = (state_35111[(2)]);
var inst_35100 = cljs.core.async.impl.protocols.closed_QMARK_.call(null,out);
var state_35111__$1 = (function (){var statearr_35138 = state_35111;
(statearr_35138[(15)] = inst_35099);

return statearr_35138;
})();
if(cljs.core.truth_(inst_35100)){
var statearr_35139_35167 = state_35111__$1;
(statearr_35139_35167[(1)] = (21));

} else {
var statearr_35140_35168 = state_35111__$1;
(statearr_35140_35168[(1)] = (22));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (5))){
var inst_35055 = cljs.core.async.close_BANG_.call(null,out);
var state_35111__$1 = state_35111;
var statearr_35141_35169 = state_35111__$1;
(statearr_35141_35169[(2)] = inst_35055);

(statearr_35141_35169[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (14))){
var inst_35077 = (state_35111[(7)]);
var inst_35079 = cljs.core.chunked_seq_QMARK_.call(null,inst_35077);
var state_35111__$1 = state_35111;
if(inst_35079){
var statearr_35142_35170 = state_35111__$1;
(statearr_35142_35170[(1)] = (17));

} else {
var statearr_35143_35171 = state_35111__$1;
(statearr_35143_35171[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (16))){
var inst_35095 = (state_35111[(2)]);
var state_35111__$1 = state_35111;
var statearr_35144_35172 = state_35111__$1;
(statearr_35144_35172[(2)] = inst_35095);

(statearr_35144_35172[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35112 === (10))){
var inst_35064 = (state_35111[(9)]);
var inst_35066 = (state_35111[(12)]);
var inst_35071 = cljs.core._nth.call(null,inst_35064,inst_35066);
var state_35111__$1 = state_35111;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35111__$1,(13),out,inst_35071);
} else {
if((state_val_35112 === (18))){
var inst_35077 = (state_35111[(7)]);
var inst_35086 = cljs.core.first.call(null,inst_35077);
var state_35111__$1 = state_35111;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35111__$1,(20),out,inst_35086);
} else {
if((state_val_35112 === (8))){
var inst_35065 = (state_35111[(10)]);
var inst_35066 = (state_35111[(12)]);
var inst_35068 = (inst_35066 < inst_35065);
var inst_35069 = inst_35068;
var state_35111__$1 = state_35111;
if(cljs.core.truth_(inst_35069)){
var statearr_35145_35173 = state_35111__$1;
(statearr_35145_35173[(1)] = (10));

} else {
var statearr_35146_35174 = state_35111__$1;
(statearr_35146_35174[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto__))
;
return ((function (switch__33350__auto__,c__33495__auto__){
return (function() {
var cljs$core$async$mapcat_STAR__$_state_machine__33351__auto__ = null;
var cljs$core$async$mapcat_STAR__$_state_machine__33351__auto____0 = (function (){
var statearr_35147 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_35147[(0)] = cljs$core$async$mapcat_STAR__$_state_machine__33351__auto__);

(statearr_35147[(1)] = (1));

return statearr_35147;
});
var cljs$core$async$mapcat_STAR__$_state_machine__33351__auto____1 = (function (state_35111){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_35111);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e35148){if((e35148 instanceof Object)){
var ex__33354__auto__ = e35148;
var statearr_35149_35175 = state_35111;
(statearr_35149_35175[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_35111);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e35148;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35176 = state_35111;
state_35111 = G__35176;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$mapcat_STAR__$_state_machine__33351__auto__ = function(state_35111){
switch(arguments.length){
case 0:
return cljs$core$async$mapcat_STAR__$_state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$mapcat_STAR__$_state_machine__33351__auto____1.call(this,state_35111);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$mapcat_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mapcat_STAR__$_state_machine__33351__auto____0;
cljs$core$async$mapcat_STAR__$_state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mapcat_STAR__$_state_machine__33351__auto____1;
return cljs$core$async$mapcat_STAR__$_state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto__))
})();
var state__33497__auto__ = (function (){var statearr_35150 = f__33496__auto__.call(null);
(statearr_35150[(6)] = c__33495__auto__);

return statearr_35150;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto__))
);

return c__33495__auto__;
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_LT_ = (function cljs$core$async$mapcat_LT_(var_args){
var G__35178 = arguments.length;
switch (G__35178) {
case 2:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2 = (function (f,in$){
return cljs.core.async.mapcat_LT_.call(null,f,in$,null);
});

cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3 = (function (f,in$,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
cljs.core.async.mapcat_STAR_.call(null,f,in$,out);

return out;
});

cljs.core.async.mapcat_LT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_GT_ = (function cljs$core$async$mapcat_GT_(var_args){
var G__35181 = arguments.length;
switch (G__35181) {
case 2:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2 = (function (f,out){
return cljs.core.async.mapcat_GT_.call(null,f,out,null);
});

cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3 = (function (f,out,buf_or_n){
var in$ = cljs.core.async.chan.call(null,buf_or_n);
cljs.core.async.mapcat_STAR_.call(null,f,in$,out);

return in$;
});

cljs.core.async.mapcat_GT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.unique = (function cljs$core$async$unique(var_args){
var G__35184 = arguments.length;
switch (G__35184) {
case 1:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1 = (function (ch){
return cljs.core.async.unique.call(null,ch,null);
});

cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2 = (function (ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__33495__auto___35231 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___35231,out){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___35231,out){
return (function (state_35208){
var state_val_35209 = (state_35208[(1)]);
if((state_val_35209 === (7))){
var inst_35203 = (state_35208[(2)]);
var state_35208__$1 = state_35208;
var statearr_35210_35232 = state_35208__$1;
(statearr_35210_35232[(2)] = inst_35203);

(statearr_35210_35232[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35209 === (1))){
var inst_35185 = null;
var state_35208__$1 = (function (){var statearr_35211 = state_35208;
(statearr_35211[(7)] = inst_35185);

return statearr_35211;
})();
var statearr_35212_35233 = state_35208__$1;
(statearr_35212_35233[(2)] = null);

(statearr_35212_35233[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35209 === (4))){
var inst_35188 = (state_35208[(8)]);
var inst_35188__$1 = (state_35208[(2)]);
var inst_35189 = (inst_35188__$1 == null);
var inst_35190 = cljs.core.not.call(null,inst_35189);
var state_35208__$1 = (function (){var statearr_35213 = state_35208;
(statearr_35213[(8)] = inst_35188__$1);

return statearr_35213;
})();
if(inst_35190){
var statearr_35214_35234 = state_35208__$1;
(statearr_35214_35234[(1)] = (5));

} else {
var statearr_35215_35235 = state_35208__$1;
(statearr_35215_35235[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35209 === (6))){
var state_35208__$1 = state_35208;
var statearr_35216_35236 = state_35208__$1;
(statearr_35216_35236[(2)] = null);

(statearr_35216_35236[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35209 === (3))){
var inst_35205 = (state_35208[(2)]);
var inst_35206 = cljs.core.async.close_BANG_.call(null,out);
var state_35208__$1 = (function (){var statearr_35217 = state_35208;
(statearr_35217[(9)] = inst_35205);

return statearr_35217;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_35208__$1,inst_35206);
} else {
if((state_val_35209 === (2))){
var state_35208__$1 = state_35208;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35208__$1,(4),ch);
} else {
if((state_val_35209 === (11))){
var inst_35188 = (state_35208[(8)]);
var inst_35197 = (state_35208[(2)]);
var inst_35185 = inst_35188;
var state_35208__$1 = (function (){var statearr_35218 = state_35208;
(statearr_35218[(7)] = inst_35185);

(statearr_35218[(10)] = inst_35197);

return statearr_35218;
})();
var statearr_35219_35237 = state_35208__$1;
(statearr_35219_35237[(2)] = null);

(statearr_35219_35237[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35209 === (9))){
var inst_35188 = (state_35208[(8)]);
var state_35208__$1 = state_35208;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35208__$1,(11),out,inst_35188);
} else {
if((state_val_35209 === (5))){
var inst_35188 = (state_35208[(8)]);
var inst_35185 = (state_35208[(7)]);
var inst_35192 = cljs.core._EQ_.call(null,inst_35188,inst_35185);
var state_35208__$1 = state_35208;
if(inst_35192){
var statearr_35221_35238 = state_35208__$1;
(statearr_35221_35238[(1)] = (8));

} else {
var statearr_35222_35239 = state_35208__$1;
(statearr_35222_35239[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35209 === (10))){
var inst_35200 = (state_35208[(2)]);
var state_35208__$1 = state_35208;
var statearr_35223_35240 = state_35208__$1;
(statearr_35223_35240[(2)] = inst_35200);

(statearr_35223_35240[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35209 === (8))){
var inst_35185 = (state_35208[(7)]);
var tmp35220 = inst_35185;
var inst_35185__$1 = tmp35220;
var state_35208__$1 = (function (){var statearr_35224 = state_35208;
(statearr_35224[(7)] = inst_35185__$1);

return statearr_35224;
})();
var statearr_35225_35241 = state_35208__$1;
(statearr_35225_35241[(2)] = null);

(statearr_35225_35241[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___35231,out))
;
return ((function (switch__33350__auto__,c__33495__auto___35231,out){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_35226 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_35226[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_35226[(1)] = (1));

return statearr_35226;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_35208){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_35208);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e35227){if((e35227 instanceof Object)){
var ex__33354__auto__ = e35227;
var statearr_35228_35242 = state_35208;
(statearr_35228_35242[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_35208);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e35227;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35243 = state_35208;
state_35208 = G__35243;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_35208){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_35208);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___35231,out))
})();
var state__33497__auto__ = (function (){var statearr_35229 = f__33496__auto__.call(null);
(statearr_35229[(6)] = c__33495__auto___35231);

return statearr_35229;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___35231,out))
);


return out;
});

cljs.core.async.unique.cljs$lang$maxFixedArity = 2;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition = (function cljs$core$async$partition(var_args){
var G__35245 = arguments.length;
switch (G__35245) {
case 2:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.partition.call(null,n,ch,null);
});

cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__33495__auto___35311 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___35311,out){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___35311,out){
return (function (state_35283){
var state_val_35284 = (state_35283[(1)]);
if((state_val_35284 === (7))){
var inst_35279 = (state_35283[(2)]);
var state_35283__$1 = state_35283;
var statearr_35285_35312 = state_35283__$1;
(statearr_35285_35312[(2)] = inst_35279);

(statearr_35285_35312[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (1))){
var inst_35246 = (new Array(n));
var inst_35247 = inst_35246;
var inst_35248 = (0);
var state_35283__$1 = (function (){var statearr_35286 = state_35283;
(statearr_35286[(7)] = inst_35247);

(statearr_35286[(8)] = inst_35248);

return statearr_35286;
})();
var statearr_35287_35313 = state_35283__$1;
(statearr_35287_35313[(2)] = null);

(statearr_35287_35313[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (4))){
var inst_35251 = (state_35283[(9)]);
var inst_35251__$1 = (state_35283[(2)]);
var inst_35252 = (inst_35251__$1 == null);
var inst_35253 = cljs.core.not.call(null,inst_35252);
var state_35283__$1 = (function (){var statearr_35288 = state_35283;
(statearr_35288[(9)] = inst_35251__$1);

return statearr_35288;
})();
if(inst_35253){
var statearr_35289_35314 = state_35283__$1;
(statearr_35289_35314[(1)] = (5));

} else {
var statearr_35290_35315 = state_35283__$1;
(statearr_35290_35315[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (15))){
var inst_35273 = (state_35283[(2)]);
var state_35283__$1 = state_35283;
var statearr_35291_35316 = state_35283__$1;
(statearr_35291_35316[(2)] = inst_35273);

(statearr_35291_35316[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (13))){
var state_35283__$1 = state_35283;
var statearr_35292_35317 = state_35283__$1;
(statearr_35292_35317[(2)] = null);

(statearr_35292_35317[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (6))){
var inst_35248 = (state_35283[(8)]);
var inst_35269 = (inst_35248 > (0));
var state_35283__$1 = state_35283;
if(cljs.core.truth_(inst_35269)){
var statearr_35293_35318 = state_35283__$1;
(statearr_35293_35318[(1)] = (12));

} else {
var statearr_35294_35319 = state_35283__$1;
(statearr_35294_35319[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (3))){
var inst_35281 = (state_35283[(2)]);
var state_35283__$1 = state_35283;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_35283__$1,inst_35281);
} else {
if((state_val_35284 === (12))){
var inst_35247 = (state_35283[(7)]);
var inst_35271 = cljs.core.vec.call(null,inst_35247);
var state_35283__$1 = state_35283;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35283__$1,(15),out,inst_35271);
} else {
if((state_val_35284 === (2))){
var state_35283__$1 = state_35283;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35283__$1,(4),ch);
} else {
if((state_val_35284 === (11))){
var inst_35263 = (state_35283[(2)]);
var inst_35264 = (new Array(n));
var inst_35247 = inst_35264;
var inst_35248 = (0);
var state_35283__$1 = (function (){var statearr_35295 = state_35283;
(statearr_35295[(7)] = inst_35247);

(statearr_35295[(8)] = inst_35248);

(statearr_35295[(10)] = inst_35263);

return statearr_35295;
})();
var statearr_35296_35320 = state_35283__$1;
(statearr_35296_35320[(2)] = null);

(statearr_35296_35320[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (9))){
var inst_35247 = (state_35283[(7)]);
var inst_35261 = cljs.core.vec.call(null,inst_35247);
var state_35283__$1 = state_35283;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35283__$1,(11),out,inst_35261);
} else {
if((state_val_35284 === (5))){
var inst_35251 = (state_35283[(9)]);
var inst_35247 = (state_35283[(7)]);
var inst_35256 = (state_35283[(11)]);
var inst_35248 = (state_35283[(8)]);
var inst_35255 = (inst_35247[inst_35248] = inst_35251);
var inst_35256__$1 = (inst_35248 + (1));
var inst_35257 = (inst_35256__$1 < n);
var state_35283__$1 = (function (){var statearr_35297 = state_35283;
(statearr_35297[(12)] = inst_35255);

(statearr_35297[(11)] = inst_35256__$1);

return statearr_35297;
})();
if(cljs.core.truth_(inst_35257)){
var statearr_35298_35321 = state_35283__$1;
(statearr_35298_35321[(1)] = (8));

} else {
var statearr_35299_35322 = state_35283__$1;
(statearr_35299_35322[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (14))){
var inst_35276 = (state_35283[(2)]);
var inst_35277 = cljs.core.async.close_BANG_.call(null,out);
var state_35283__$1 = (function (){var statearr_35301 = state_35283;
(statearr_35301[(13)] = inst_35276);

return statearr_35301;
})();
var statearr_35302_35323 = state_35283__$1;
(statearr_35302_35323[(2)] = inst_35277);

(statearr_35302_35323[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (10))){
var inst_35267 = (state_35283[(2)]);
var state_35283__$1 = state_35283;
var statearr_35303_35324 = state_35283__$1;
(statearr_35303_35324[(2)] = inst_35267);

(statearr_35303_35324[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35284 === (8))){
var inst_35247 = (state_35283[(7)]);
var inst_35256 = (state_35283[(11)]);
var tmp35300 = inst_35247;
var inst_35247__$1 = tmp35300;
var inst_35248 = inst_35256;
var state_35283__$1 = (function (){var statearr_35304 = state_35283;
(statearr_35304[(7)] = inst_35247__$1);

(statearr_35304[(8)] = inst_35248);

return statearr_35304;
})();
var statearr_35305_35325 = state_35283__$1;
(statearr_35305_35325[(2)] = null);

(statearr_35305_35325[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___35311,out))
;
return ((function (switch__33350__auto__,c__33495__auto___35311,out){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_35306 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_35306[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_35306[(1)] = (1));

return statearr_35306;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_35283){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_35283);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e35307){if((e35307 instanceof Object)){
var ex__33354__auto__ = e35307;
var statearr_35308_35326 = state_35283;
(statearr_35308_35326[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_35283);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e35307;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35327 = state_35283;
state_35283 = G__35327;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_35283){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_35283);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___35311,out))
})();
var state__33497__auto__ = (function (){var statearr_35309 = f__33496__auto__.call(null);
(statearr_35309[(6)] = c__33495__auto___35311);

return statearr_35309;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___35311,out))
);


return out;
});

cljs.core.async.partition.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition_by = (function cljs$core$async$partition_by(var_args){
var G__35329 = arguments.length;
switch (G__35329) {
case 2:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2 = (function (f,ch){
return cljs.core.async.partition_by.call(null,f,ch,null);
});

cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3 = (function (f,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__33495__auto___35399 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__33495__auto___35399,out){
return (function (){
var f__33496__auto__ = (function (){var switch__33350__auto__ = ((function (c__33495__auto___35399,out){
return (function (state_35371){
var state_val_35372 = (state_35371[(1)]);
if((state_val_35372 === (7))){
var inst_35367 = (state_35371[(2)]);
var state_35371__$1 = state_35371;
var statearr_35373_35400 = state_35371__$1;
(statearr_35373_35400[(2)] = inst_35367);

(statearr_35373_35400[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (1))){
var inst_35330 = [];
var inst_35331 = inst_35330;
var inst_35332 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);
var state_35371__$1 = (function (){var statearr_35374 = state_35371;
(statearr_35374[(7)] = inst_35332);

(statearr_35374[(8)] = inst_35331);

return statearr_35374;
})();
var statearr_35375_35401 = state_35371__$1;
(statearr_35375_35401[(2)] = null);

(statearr_35375_35401[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (4))){
var inst_35335 = (state_35371[(9)]);
var inst_35335__$1 = (state_35371[(2)]);
var inst_35336 = (inst_35335__$1 == null);
var inst_35337 = cljs.core.not.call(null,inst_35336);
var state_35371__$1 = (function (){var statearr_35376 = state_35371;
(statearr_35376[(9)] = inst_35335__$1);

return statearr_35376;
})();
if(inst_35337){
var statearr_35377_35402 = state_35371__$1;
(statearr_35377_35402[(1)] = (5));

} else {
var statearr_35378_35403 = state_35371__$1;
(statearr_35378_35403[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (15))){
var inst_35361 = (state_35371[(2)]);
var state_35371__$1 = state_35371;
var statearr_35379_35404 = state_35371__$1;
(statearr_35379_35404[(2)] = inst_35361);

(statearr_35379_35404[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (13))){
var state_35371__$1 = state_35371;
var statearr_35380_35405 = state_35371__$1;
(statearr_35380_35405[(2)] = null);

(statearr_35380_35405[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (6))){
var inst_35331 = (state_35371[(8)]);
var inst_35356 = inst_35331.length;
var inst_35357 = (inst_35356 > (0));
var state_35371__$1 = state_35371;
if(cljs.core.truth_(inst_35357)){
var statearr_35381_35406 = state_35371__$1;
(statearr_35381_35406[(1)] = (12));

} else {
var statearr_35382_35407 = state_35371__$1;
(statearr_35382_35407[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (3))){
var inst_35369 = (state_35371[(2)]);
var state_35371__$1 = state_35371;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_35371__$1,inst_35369);
} else {
if((state_val_35372 === (12))){
var inst_35331 = (state_35371[(8)]);
var inst_35359 = cljs.core.vec.call(null,inst_35331);
var state_35371__$1 = state_35371;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35371__$1,(15),out,inst_35359);
} else {
if((state_val_35372 === (2))){
var state_35371__$1 = state_35371;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_35371__$1,(4),ch);
} else {
if((state_val_35372 === (11))){
var inst_35335 = (state_35371[(9)]);
var inst_35339 = (state_35371[(10)]);
var inst_35349 = (state_35371[(2)]);
var inst_35350 = [];
var inst_35351 = inst_35350.push(inst_35335);
var inst_35331 = inst_35350;
var inst_35332 = inst_35339;
var state_35371__$1 = (function (){var statearr_35383 = state_35371;
(statearr_35383[(11)] = inst_35349);

(statearr_35383[(7)] = inst_35332);

(statearr_35383[(12)] = inst_35351);

(statearr_35383[(8)] = inst_35331);

return statearr_35383;
})();
var statearr_35384_35408 = state_35371__$1;
(statearr_35384_35408[(2)] = null);

(statearr_35384_35408[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (9))){
var inst_35331 = (state_35371[(8)]);
var inst_35347 = cljs.core.vec.call(null,inst_35331);
var state_35371__$1 = state_35371;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_35371__$1,(11),out,inst_35347);
} else {
if((state_val_35372 === (5))){
var inst_35335 = (state_35371[(9)]);
var inst_35332 = (state_35371[(7)]);
var inst_35339 = (state_35371[(10)]);
var inst_35339__$1 = f.call(null,inst_35335);
var inst_35340 = cljs.core._EQ_.call(null,inst_35339__$1,inst_35332);
var inst_35341 = cljs.core.keyword_identical_QMARK_.call(null,inst_35332,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));
var inst_35342 = ((inst_35340) || (inst_35341));
var state_35371__$1 = (function (){var statearr_35385 = state_35371;
(statearr_35385[(10)] = inst_35339__$1);

return statearr_35385;
})();
if(cljs.core.truth_(inst_35342)){
var statearr_35386_35409 = state_35371__$1;
(statearr_35386_35409[(1)] = (8));

} else {
var statearr_35387_35410 = state_35371__$1;
(statearr_35387_35410[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (14))){
var inst_35364 = (state_35371[(2)]);
var inst_35365 = cljs.core.async.close_BANG_.call(null,out);
var state_35371__$1 = (function (){var statearr_35389 = state_35371;
(statearr_35389[(13)] = inst_35364);

return statearr_35389;
})();
var statearr_35390_35411 = state_35371__$1;
(statearr_35390_35411[(2)] = inst_35365);

(statearr_35390_35411[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (10))){
var inst_35354 = (state_35371[(2)]);
var state_35371__$1 = state_35371;
var statearr_35391_35412 = state_35371__$1;
(statearr_35391_35412[(2)] = inst_35354);

(statearr_35391_35412[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_35372 === (8))){
var inst_35335 = (state_35371[(9)]);
var inst_35339 = (state_35371[(10)]);
var inst_35331 = (state_35371[(8)]);
var inst_35344 = inst_35331.push(inst_35335);
var tmp35388 = inst_35331;
var inst_35331__$1 = tmp35388;
var inst_35332 = inst_35339;
var state_35371__$1 = (function (){var statearr_35392 = state_35371;
(statearr_35392[(14)] = inst_35344);

(statearr_35392[(7)] = inst_35332);

(statearr_35392[(8)] = inst_35331__$1);

return statearr_35392;
})();
var statearr_35393_35413 = state_35371__$1;
(statearr_35393_35413[(2)] = null);

(statearr_35393_35413[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__33495__auto___35399,out))
;
return ((function (switch__33350__auto__,c__33495__auto___35399,out){
return (function() {
var cljs$core$async$state_machine__33351__auto__ = null;
var cljs$core$async$state_machine__33351__auto____0 = (function (){
var statearr_35394 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_35394[(0)] = cljs$core$async$state_machine__33351__auto__);

(statearr_35394[(1)] = (1));

return statearr_35394;
});
var cljs$core$async$state_machine__33351__auto____1 = (function (state_35371){
while(true){
var ret_value__33352__auto__ = (function (){try{while(true){
var result__33353__auto__ = switch__33350__auto__.call(null,state_35371);
if(cljs.core.keyword_identical_QMARK_.call(null,result__33353__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__33353__auto__;
}
break;
}
}catch (e35395){if((e35395 instanceof Object)){
var ex__33354__auto__ = e35395;
var statearr_35396_35414 = state_35371;
(statearr_35396_35414[(5)] = ex__33354__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_35371);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e35395;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__33352__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__35415 = state_35371;
state_35371 = G__35415;
continue;
} else {
return ret_value__33352__auto__;
}
break;
}
});
cljs$core$async$state_machine__33351__auto__ = function(state_35371){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__33351__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__33351__auto____1.call(this,state_35371);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__33351__auto____0;
cljs$core$async$state_machine__33351__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__33351__auto____1;
return cljs$core$async$state_machine__33351__auto__;
})()
;})(switch__33350__auto__,c__33495__auto___35399,out))
})();
var state__33497__auto__ = (function (){var statearr_35397 = f__33496__auto__.call(null);
(statearr_35397[(6)] = c__33495__auto___35399);

return statearr_35397;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__33497__auto__);
});})(c__33495__auto___35399,out))
);


return out;
});

cljs.core.async.partition_by.cljs$lang$maxFixedArity = 3;


//# sourceMappingURL=async.js.map?rel=1642872179408
