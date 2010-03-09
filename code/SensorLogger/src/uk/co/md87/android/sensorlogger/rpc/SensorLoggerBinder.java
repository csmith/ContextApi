/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: src/uk/co/md87/android/sensorlogger/rpc/SensorLoggerBinder.aidl
 */
package uk.co.md87.android.sensorlogger.rpc;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
/**
 *
 * @author chris
 */
public interface SensorLoggerBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder
{
private static final java.lang.String DESCRIPTOR = "uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an SensorLoggerBinder interface,
 * generating a proxy if needed.
 */
public static uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder))) {
return ((uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder)iin);
}
return new uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder.Stub.Proxy(obj);
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
case TRANSACTION_setState:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.setState(_arg0);
reply.writeNoException();
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
case TRANSACTION_submit:
{
data.enforceInterface(DESCRIPTOR);
this.submit();
reply.writeNoException();
return true;
}
case TRANSACTION_submitWithCorrection:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.submitWithCorrection(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getClassification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getClassification();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getState:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getState();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCountdownTime:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCountdownTime();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements uk.co.md87.android.sensorlogger.rpc.SensorLoggerBinder
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
/**
     * Sets the state of the sensor binder application.
     *
     * 1 - introduction (not running)
     * 2 - countdown phase
     * 3 - collection phase
     * 4 - analysis phase
     * 5 - finished
     * 6 - uploading/uploaded
     */
public void setState(int state) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(state);
mRemote.transact(Stub.TRANSACTION_setState, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
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
public void submit() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_submit, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void submitWithCorrection(java.lang.String correction) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(correction);
mRemote.transact(Stub.TRANSACTION_submitWithCorrection, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String getClassification() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getClassification, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getState, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCountdownTime() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCountdownTime, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_setState = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_submitClassification = (IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_submit = (IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_submitWithCorrection = (IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getClassification = (IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getState = (IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getCountdownTime = (IBinder.FIRST_CALL_TRANSACTION + 6);
}
/**
     * Sets the state of the sensor binder application.
     *
     * 1 - introduction (not running)
     * 2 - countdown phase
     * 3 - collection phase
     * 4 - analysis phase
     * 5 - finished
     * 6 - uploading/uploaded
     */
public void setState(int state) throws android.os.RemoteException;
public void submitClassification(java.lang.String classification) throws android.os.RemoteException;
public void submit() throws android.os.RemoteException;
public void submitWithCorrection(java.lang.String correction) throws android.os.RemoteException;
public java.lang.String getClassification() throws android.os.RemoteException;
public int getState() throws android.os.RemoteException;
public int getCountdownTime() throws android.os.RemoteException;
}
