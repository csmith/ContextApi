/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: uk/co/md87/android/activityrecorder/rpc/ActivityRecorderBinder.aidl
 */
package uk.co.md87.android.activityrecorder.rpc;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import java.util.List;
/**
 *
 * @author chris
 */
public interface ActivityRecorderBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder
{
private static final java.lang.String DESCRIPTOR = "uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an ActivityRecorderBinder interface,
 * generating a proxy if needed.
 */
public static uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder))) {
return ((uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder)iin);
}
return new uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder.Stub.Proxy(obj);
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
case TRANSACTION_isRunning:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isRunning();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_submitClassification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.submitClassification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getClassifications:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<uk.co.md87.android.activityrecorder.rpc.Classification> _result = this.getClassifications();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements uk.co.md87.android.activityrecorder.rpc.ActivityRecorderBinder
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
public boolean isRunning() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isRunning, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void submitClassification(java.lang.String classification) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(classification);
mRemote.transact(Stub.TRANSACTION_submitClassification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.util.List<uk.co.md87.android.activityrecorder.rpc.Classification> getClassifications() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<uk.co.md87.android.activityrecorder.rpc.Classification> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getClassifications, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(uk.co.md87.android.activityrecorder.rpc.Classification.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_isRunning = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_submitClassification = (IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getClassifications = (IBinder.FIRST_CALL_TRANSACTION + 2);
}
public boolean isRunning() throws android.os.RemoteException;
public void submitClassification(java.lang.String classification) throws android.os.RemoteException;
public java.util.List<uk.co.md87.android.activityrecorder.rpc.Classification> getClassifications() throws android.os.RemoteException;
}
