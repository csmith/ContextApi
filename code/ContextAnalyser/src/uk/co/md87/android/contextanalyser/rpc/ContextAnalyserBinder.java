/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: uk/co/md87/android/contextanalyser/rpc/ContextAnalyserBinder.aidl
 */
package uk.co.md87.android.contextanalyser.rpc;
import java.lang.String;
import java.util.Map;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
/**
 * Interface to facilitate RPC with the context analyser service.
 *
 * @author chris
 */
public interface ContextAnalyserBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder
{
private static final java.lang.String DESCRIPTOR = "uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an ContextAnalyserBinder interface,
 * generating a proxy if needed.
 */
public static uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder))) {
return ((uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder)iin);
}
return new uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getActivity:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getActivity();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getPredictions:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _result = this.getPredictions();
reply.writeNoException();
reply.writeMap(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements uk.co.md87.android.contextanalyser.rpc.ContextAnalyserBinder
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.lang.String getActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getActivity, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.util.Map getPredictions() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPredictions, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getActivity = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getPredictions = (IBinder.FIRST_CALL_TRANSACTION + 1);
}
public java.lang.String getActivity() throws android.os.RemoteException;
public java.util.Map getPredictions() throws android.os.RemoteException;
}
