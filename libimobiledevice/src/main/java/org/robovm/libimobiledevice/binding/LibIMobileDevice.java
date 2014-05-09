/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.robovm.libimobiledevice.binding;

public class LibIMobileDevice implements LibIMobileDeviceConstants {
  public static PlistRef plist_new_dict() {
    return new PlistRef(LibIMobileDeviceJNI.plist_new_dict(), true);
  }

  public static void plist_free(PlistRef plist) {
    LibIMobileDeviceJNI.plist_free(PlistRef.getCPtr(plist));
  }

  public static void plist_to_bin(PlistRef plist, ByteArrayOut plist_bin, IntOut length) {
    LibIMobileDeviceJNI.plist_to_bin(PlistRef.getCPtr(plist), ByteArrayOut.getCPtr(plist_bin), plist_bin, IntOut.getCPtr(length), length);
  }

  public static void plist_to_xml(PlistRef plist, ByteArrayOut plist_xml, IntOut length) {
    LibIMobileDeviceJNI.plist_to_xml(PlistRef.getCPtr(plist), ByteArrayOut.getCPtr(plist_xml), plist_xml, IntOut.getCPtr(length), length);
  }

  public static void plist_from_bin(byte[] plist_bin, int length, PlistRefOut plist) {
    LibIMobileDeviceJNI.plist_from_bin(plist_bin, length, PlistRefOut.getCPtr(plist), plist);
  }

  public static void delete_StringOut_value(StringOut s) {
    LibIMobileDeviceJNI.delete_StringOut_value(StringOut.getCPtr(s), s);
  }

  public static void delete_ByteArrayOut_value(ByteArrayOut s) {
    LibIMobileDeviceJNI.delete_ByteArrayOut_value(ByteArrayOut.getCPtr(s), s);
  }

  public static void delete_StringArray_values(StringArray s, int length) {
    LibIMobileDeviceJNI.delete_StringArray_values(StringArray.getCPtr(s), s, length);
  }

  public static void delete_StringArray_values_z(StringArray s) {
    LibIMobileDeviceJNI.delete_StringArray_values_z(StringArray.getCPtr(s), s);
  }

  public static long get_global_instproxy_status_cb() {
    return LibIMobileDeviceJNI.get_global_instproxy_status_cb();
  }

  public static long get_global_idevice_event_cb() {
    return LibIMobileDeviceJNI.get_global_idevice_event_cb();
  }

  public static short upload_image(MobileImageMounterClientRef client, String image_path, String image_type) {
    return LibIMobileDeviceJNI.upload_image(MobileImageMounterClientRef.getCPtr(client), image_path, image_type);
  }

  public static void idevice_set_debug_level(int level) {
    LibIMobileDeviceJNI.idevice_set_debug_level(level);
  }

  public static short idevice_event_subscribe(long callback, int user_data) {
    return LibIMobileDeviceJNI.idevice_event_subscribe(callback, user_data);
  }

  public static short idevice_event_unsubscribe() {
    return LibIMobileDeviceJNI.idevice_event_unsubscribe();
  }

  public static short idevice_get_device_list(StringArrayOut devices, IntOut count) {
    return LibIMobileDeviceJNI.idevice_get_device_list(StringArrayOut.getCPtr(devices), devices, IntOut.getCPtr(count), count);
  }

  public static short idevice_device_list_free(StringArray devices) {
    return LibIMobileDeviceJNI.idevice_device_list_free(StringArray.getCPtr(devices), devices);
  }

  public static short idevice_new(IDeviceRefOut device, String udid) {
    return LibIMobileDeviceJNI.idevice_new(IDeviceRefOut.getCPtr(device), device, udid);
  }

  public static short idevice_free(IDeviceRef device) {
    return LibIMobileDeviceJNI.idevice_free(IDeviceRef.getCPtr(device));
  }

  public static short idevice_connect(IDeviceRef device, short port, IDeviceConnectionRefOut connection) {
    return LibIMobileDeviceJNI.idevice_connect(IDeviceRef.getCPtr(device), port, IDeviceConnectionRefOut.getCPtr(connection), connection);
  }

  public static short idevice_disconnect(IDeviceConnectionRef connection) {
    return LibIMobileDeviceJNI.idevice_disconnect(IDeviceConnectionRef.getCPtr(connection));
  }

  public static short idevice_connection_send(IDeviceConnectionRef connection, byte[] data, int len, IntOut sent_bytes) {
    return LibIMobileDeviceJNI.idevice_connection_send(IDeviceConnectionRef.getCPtr(connection), data, len, IntOut.getCPtr(sent_bytes), sent_bytes);
  }

  public static short idevice_connection_receive_timeout(IDeviceConnectionRef connection, byte[] data, int len, IntOut recv_bytes, long timeout) {
    return LibIMobileDeviceJNI.idevice_connection_receive_timeout(IDeviceConnectionRef.getCPtr(connection), data, len, IntOut.getCPtr(recv_bytes), recv_bytes, timeout);
  }

  public static short idevice_connection_receive(IDeviceConnectionRef connection, byte[] data, int len, IntOut recv_bytes) {
    return LibIMobileDeviceJNI.idevice_connection_receive(IDeviceConnectionRef.getCPtr(connection), data, len, IntOut.getCPtr(recv_bytes), recv_bytes);
  }

  public static short idevice_connection_enable_ssl(IDeviceConnectionRef connection) {
    return LibIMobileDeviceJNI.idevice_connection_enable_ssl(IDeviceConnectionRef.getCPtr(connection));
  }

  public static short idevice_connection_disable_ssl(IDeviceConnectionRef connection) {
    return LibIMobileDeviceJNI.idevice_connection_disable_ssl(IDeviceConnectionRef.getCPtr(connection));
  }

  public static short idevice_get_handle(IDeviceRef device, IntOut handle) {
    return LibIMobileDeviceJNI.idevice_get_handle(IDeviceRef.getCPtr(device), IntOut.getCPtr(handle), handle);
  }

  public static short idevice_get_udid(IDeviceRef device, StringOut udid) {
    return LibIMobileDeviceJNI.idevice_get_udid(IDeviceRef.getCPtr(device), StringOut.getCPtr(udid), udid);
  }

  public static short lockdownd_client_new(IDeviceRef device, LockdowndClientRefOut client, String label) {
    return LibIMobileDeviceJNI.lockdownd_client_new(IDeviceRef.getCPtr(device), LockdowndClientRefOut.getCPtr(client), client, label);
  }

  public static short lockdownd_client_new_with_handshake(IDeviceRef device, LockdowndClientRefOut client, String label) {
    return LibIMobileDeviceJNI.lockdownd_client_new_with_handshake(IDeviceRef.getCPtr(device), LockdowndClientRefOut.getCPtr(client), client, label);
  }

  public static short lockdownd_client_free(LockdowndClientRef client) {
    return LibIMobileDeviceJNI.lockdownd_client_free(LockdowndClientRef.getCPtr(client));
  }

  public static short lockdownd_query_type(LockdowndClientRef client, StringOut type) {
    return LibIMobileDeviceJNI.lockdownd_query_type(LockdowndClientRef.getCPtr(client), StringOut.getCPtr(type), type);
  }

  public static short lockdownd_get_value(LockdowndClientRef client, String domain, String key, PlistRefOut value) {
    return LibIMobileDeviceJNI.lockdownd_get_value(LockdowndClientRef.getCPtr(client), domain, key, PlistRefOut.getCPtr(value), value);
  }

  public static short lockdownd_set_value(LockdowndClientRef client, String domain, String key, PlistRef value) {
    return LibIMobileDeviceJNI.lockdownd_set_value(LockdowndClientRef.getCPtr(client), domain, key, PlistRef.getCPtr(value));
  }

  public static short lockdownd_remove_value(LockdowndClientRef client, String domain, String key) {
    return LibIMobileDeviceJNI.lockdownd_remove_value(LockdowndClientRef.getCPtr(client), domain, key);
  }

  public static short lockdownd_start_service(LockdowndClientRef client, String identifier, LockdowndServiceDescriptorStructOut service) {
    return LibIMobileDeviceJNI.lockdownd_start_service(LockdowndClientRef.getCPtr(client), identifier, LockdowndServiceDescriptorStructOut.getCPtr(service), service);
  }

  public static short lockdownd_start_session(LockdowndClientRef client, String host_id, StringOut session_id, IntOut ssl_enabled) {
    return LibIMobileDeviceJNI.lockdownd_start_session(LockdowndClientRef.getCPtr(client), host_id, StringOut.getCPtr(session_id), session_id, IntOut.getCPtr(ssl_enabled), ssl_enabled);
  }

  public static short lockdownd_stop_session(LockdowndClientRef client, String session_id) {
    return LibIMobileDeviceJNI.lockdownd_stop_session(LockdowndClientRef.getCPtr(client), session_id);
  }

  public static short lockdownd_send(LockdowndClientRef client, PlistRef plist) {
    return LibIMobileDeviceJNI.lockdownd_send(LockdowndClientRef.getCPtr(client), PlistRef.getCPtr(plist));
  }

  public static short lockdownd_receive(LockdowndClientRef client, PlistRefOut plist) {
    return LibIMobileDeviceJNI.lockdownd_receive(LockdowndClientRef.getCPtr(client), PlistRefOut.getCPtr(plist), plist);
  }

  public static short lockdownd_pair(LockdowndClientRef client, LockdowndPairRecordStruct pair_record) {
    return LibIMobileDeviceJNI.lockdownd_pair(LockdowndClientRef.getCPtr(client), LockdowndPairRecordStruct.getCPtr(pair_record), pair_record);
  }

  public static short lockdownd_validate_pair(LockdowndClientRef client, LockdowndPairRecordStruct pair_record) {
    return LibIMobileDeviceJNI.lockdownd_validate_pair(LockdowndClientRef.getCPtr(client), LockdowndPairRecordStruct.getCPtr(pair_record), pair_record);
  }

  public static short lockdownd_unpair(LockdowndClientRef client, LockdowndPairRecordStruct pair_record) {
    return LibIMobileDeviceJNI.lockdownd_unpair(LockdowndClientRef.getCPtr(client), LockdowndPairRecordStruct.getCPtr(pair_record), pair_record);
  }

  public static short lockdownd_activate(LockdowndClientRef client, PlistRef activation_record) {
    return LibIMobileDeviceJNI.lockdownd_activate(LockdowndClientRef.getCPtr(client), PlistRef.getCPtr(activation_record));
  }

  public static short lockdownd_deactivate(LockdowndClientRef client) {
    return LibIMobileDeviceJNI.lockdownd_deactivate(LockdowndClientRef.getCPtr(client));
  }

  public static short lockdownd_enter_recovery(LockdowndClientRef client) {
    return LibIMobileDeviceJNI.lockdownd_enter_recovery(LockdowndClientRef.getCPtr(client));
  }

  public static short lockdownd_goodbye(LockdowndClientRef client) {
    return LibIMobileDeviceJNI.lockdownd_goodbye(LockdowndClientRef.getCPtr(client));
  }

  public static void lockdownd_client_set_label(LockdowndClientRef client, String label) {
    LibIMobileDeviceJNI.lockdownd_client_set_label(LockdowndClientRef.getCPtr(client), label);
  }

  public static short lockdownd_get_device_udid(LockdowndClientRef control, StringOut udid) {
    return LibIMobileDeviceJNI.lockdownd_get_device_udid(LockdowndClientRef.getCPtr(control), StringOut.getCPtr(udid), udid);
  }

  public static short lockdownd_get_device_name(LockdowndClientRef client, StringOut device_name) {
    return LibIMobileDeviceJNI.lockdownd_get_device_name(LockdowndClientRef.getCPtr(client), StringOut.getCPtr(device_name), device_name);
  }

  public static short lockdownd_get_sync_data_classes(LockdowndClientRef client, StringArrayOut classes, IntOut count) {
    return LibIMobileDeviceJNI.lockdownd_get_sync_data_classes(LockdowndClientRef.getCPtr(client), StringArrayOut.getCPtr(classes), classes, IntOut.getCPtr(count), count);
  }

  public static short lockdownd_data_classes_free(StringArray classes) {
    return LibIMobileDeviceJNI.lockdownd_data_classes_free(StringArray.getCPtr(classes), classes);
  }

  public static short lockdownd_service_descriptor_free(LockdowndServiceDescriptorStruct service) {
    return LibIMobileDeviceJNI.lockdownd_service_descriptor_free(LockdowndServiceDescriptorStruct.getCPtr(service), service);
  }

  public static short afc_client_new(IDeviceRef device, LockdowndServiceDescriptorStruct service, AfcClientRefOut client) {
    return LibIMobileDeviceJNI.afc_client_new(IDeviceRef.getCPtr(device), LockdowndServiceDescriptorStruct.getCPtr(service), service, AfcClientRefOut.getCPtr(client), client);
  }

  public static short afc_client_start_service(IDeviceRef device, AfcClientRefOut client, String label) {
    return LibIMobileDeviceJNI.afc_client_start_service(IDeviceRef.getCPtr(device), AfcClientRefOut.getCPtr(client), client, label);
  }

  public static short afc_client_free(AfcClientRef client) {
    return LibIMobileDeviceJNI.afc_client_free(AfcClientRef.getCPtr(client));
  }

  public static short afc_get_device_info(AfcClientRef client, StringArrayOut infos) {
    return LibIMobileDeviceJNI.afc_get_device_info(AfcClientRef.getCPtr(client), StringArrayOut.getCPtr(infos), infos);
  }

  public static short afc_read_directory(AfcClientRef client, String dir, StringArrayOut list) {
    return LibIMobileDeviceJNI.afc_read_directory(AfcClientRef.getCPtr(client), dir, StringArrayOut.getCPtr(list), list);
  }

  public static short afc_get_file_info(AfcClientRef client, String filename, StringArrayOut infolist) {
    return LibIMobileDeviceJNI.afc_get_file_info(AfcClientRef.getCPtr(client), filename, StringArrayOut.getCPtr(infolist), infolist);
  }

  public static short afc_file_open(AfcClientRef client, String filename, AfcFileMode file_mode, LongOut handle) {
    return LibIMobileDeviceJNI.afc_file_open(AfcClientRef.getCPtr(client), filename, file_mode.swigValue(), LongOut.getCPtr(handle), handle);
  }

  public static short afc_file_close(AfcClientRef client, long handle) {
    return LibIMobileDeviceJNI.afc_file_close(AfcClientRef.getCPtr(client), handle);
  }

  public static short afc_file_lock(AfcClientRef client, long handle, AfcLockOperation operation) {
    return LibIMobileDeviceJNI.afc_file_lock(AfcClientRef.getCPtr(client), handle, operation.swigValue());
  }

  public static short afc_file_read(AfcClientRef client, long handle, byte[] data, int length, IntOut bytes_read) {
    return LibIMobileDeviceJNI.afc_file_read(AfcClientRef.getCPtr(client), handle, data, length, IntOut.getCPtr(bytes_read), bytes_read);
  }

  public static short afc_file_write(AfcClientRef client, long handle, byte[] data, int length, IntOut bytes_written) {
    return LibIMobileDeviceJNI.afc_file_write(AfcClientRef.getCPtr(client), handle, data, length, IntOut.getCPtr(bytes_written), bytes_written);
  }

  public static short afc_file_seek(AfcClientRef client, long handle, long offset, int whence) {
    return LibIMobileDeviceJNI.afc_file_seek(AfcClientRef.getCPtr(client), handle, offset, whence);
  }

  public static short afc_file_tell(AfcClientRef client, long handle, LongOut position) {
    return LibIMobileDeviceJNI.afc_file_tell(AfcClientRef.getCPtr(client), handle, LongOut.getCPtr(position), position);
  }

  public static short afc_file_truncate(AfcClientRef client, long handle, long newsize) {
    return LibIMobileDeviceJNI.afc_file_truncate(AfcClientRef.getCPtr(client), handle, newsize);
  }

  public static short afc_remove_path(AfcClientRef client, String path) {
    return LibIMobileDeviceJNI.afc_remove_path(AfcClientRef.getCPtr(client), path);
  }

  public static short afc_rename_path(AfcClientRef client, String from, String to) {
    return LibIMobileDeviceJNI.afc_rename_path(AfcClientRef.getCPtr(client), from, to);
  }

  public static short afc_make_directory(AfcClientRef client, String dir) {
    return LibIMobileDeviceJNI.afc_make_directory(AfcClientRef.getCPtr(client), dir);
  }

  public static short afc_truncate(AfcClientRef client, String path, long newsize) {
    return LibIMobileDeviceJNI.afc_truncate(AfcClientRef.getCPtr(client), path, newsize);
  }

  public static short afc_make_link(AfcClientRef client, AfcLinkType linktype, String target, String linkname) {
    return LibIMobileDeviceJNI.afc_make_link(AfcClientRef.getCPtr(client), linktype.swigValue(), target, linkname);
  }

  public static short afc_set_file_time(AfcClientRef client, String path, long mtime) {
    return LibIMobileDeviceJNI.afc_set_file_time(AfcClientRef.getCPtr(client), path, mtime);
  }

  public static short afc_get_device_info_key(AfcClientRef client, String key, StringOut value) {
    return LibIMobileDeviceJNI.afc_get_device_info_key(AfcClientRef.getCPtr(client), key, StringOut.getCPtr(value), value);
  }

  public static short afc_dictionary_free(StringOut dictionary) {
    return LibIMobileDeviceJNI.afc_dictionary_free(StringOut.getCPtr(dictionary), dictionary);
  }

  public static short instproxy_client_new(IDeviceRef device, LockdowndServiceDescriptorStruct service, InstproxyClientRefOut client) {
    return LibIMobileDeviceJNI.instproxy_client_new(IDeviceRef.getCPtr(device), LockdowndServiceDescriptorStruct.getCPtr(service), service, InstproxyClientRefOut.getCPtr(client), client);
  }

  public static short instproxy_client_start_service(IDeviceRef device, InstproxyClientRefOut client, String label) {
    return LibIMobileDeviceJNI.instproxy_client_start_service(IDeviceRef.getCPtr(device), InstproxyClientRefOut.getCPtr(client), client, label);
  }

  public static short instproxy_client_free(InstproxyClientRef client) {
    return LibIMobileDeviceJNI.instproxy_client_free(InstproxyClientRef.getCPtr(client));
  }

  public static short instproxy_browse(InstproxyClientRef client, PlistRef client_options, PlistRefOut result) {
    return LibIMobileDeviceJNI.instproxy_browse(InstproxyClientRef.getCPtr(client), PlistRef.getCPtr(client_options), PlistRefOut.getCPtr(result), result);
  }

  public static short instproxy_install(InstproxyClientRef client, String pkg_path, PlistRef client_options, long status_cb, int user_data) {
    return LibIMobileDeviceJNI.instproxy_install(InstproxyClientRef.getCPtr(client), pkg_path, PlistRef.getCPtr(client_options), status_cb, user_data);
  }

  public static short instproxy_upgrade(InstproxyClientRef client, String pkg_path, PlistRef client_options, long status_cb, int user_data) {
    return LibIMobileDeviceJNI.instproxy_upgrade(InstproxyClientRef.getCPtr(client), pkg_path, PlistRef.getCPtr(client_options), status_cb, user_data);
  }

  public static short instproxy_uninstall(InstproxyClientRef client, String appid, PlistRef client_options, long status_cb, int user_data) {
    return LibIMobileDeviceJNI.instproxy_uninstall(InstproxyClientRef.getCPtr(client), appid, PlistRef.getCPtr(client_options), status_cb, user_data);
  }

  public static short instproxy_lookup_archives(InstproxyClientRef client, PlistRef client_options, PlistRefOut result) {
    return LibIMobileDeviceJNI.instproxy_lookup_archives(InstproxyClientRef.getCPtr(client), PlistRef.getCPtr(client_options), PlistRefOut.getCPtr(result), result);
  }

  public static short instproxy_archive(InstproxyClientRef client, String appid, PlistRef client_options, long status_cb, int user_data) {
    return LibIMobileDeviceJNI.instproxy_archive(InstproxyClientRef.getCPtr(client), appid, PlistRef.getCPtr(client_options), status_cb, user_data);
  }

  public static short instproxy_restore(InstproxyClientRef client, String appid, PlistRef client_options, long status_cb, int user_data) {
    return LibIMobileDeviceJNI.instproxy_restore(InstproxyClientRef.getCPtr(client), appid, PlistRef.getCPtr(client_options), status_cb, user_data);
  }

  public static short instproxy_remove_archive(InstproxyClientRef client, String appid, PlistRef client_options, long status_cb, int user_data) {
    return LibIMobileDeviceJNI.instproxy_remove_archive(InstproxyClientRef.getCPtr(client), appid, PlistRef.getCPtr(client_options), status_cb, user_data);
  }

  public static PlistRef instproxy_client_options_new() {
    return new PlistRef(LibIMobileDeviceJNI.instproxy_client_options_new(), true);
  }

  public static void instproxy_client_options_free(PlistRef client_options) {
    LibIMobileDeviceJNI.instproxy_client_options_free(PlistRef.getCPtr(client_options));
  }

  public static short instproxy_client_get_path_for_bundle_identifier(InstproxyClientRef client, String bundle_id, StringOut path) {
    return LibIMobileDeviceJNI.instproxy_client_get_path_for_bundle_identifier(InstproxyClientRef.getCPtr(client), bundle_id, StringOut.getCPtr(path), path);
  }

  public static short mobile_image_mounter_new(IDeviceRef device, LockdowndServiceDescriptorStruct service, MobileImageMounterClientRefOut client) {
    return LibIMobileDeviceJNI.mobile_image_mounter_new(IDeviceRef.getCPtr(device), LockdowndServiceDescriptorStruct.getCPtr(service), service, MobileImageMounterClientRefOut.getCPtr(client), client);
  }

  public static short mobile_image_mounter_start_service(IDeviceRef device, MobileImageMounterClientRefOut client, String label) {
    return LibIMobileDeviceJNI.mobile_image_mounter_start_service(IDeviceRef.getCPtr(device), MobileImageMounterClientRefOut.getCPtr(client), client, label);
  }

  public static short mobile_image_mounter_free(MobileImageMounterClientRef client) {
    return LibIMobileDeviceJNI.mobile_image_mounter_free(MobileImageMounterClientRef.getCPtr(client));
  }

  public static short mobile_image_mounter_lookup_image(MobileImageMounterClientRef client, String image_type, PlistRefOut result) {
    return LibIMobileDeviceJNI.mobile_image_mounter_lookup_image(MobileImageMounterClientRef.getCPtr(client), image_type, PlistRefOut.getCPtr(result), result);
  }

  public static short mobile_image_mounter_mount_image(MobileImageMounterClientRef client, String image_path, byte[] image_signature, short signature_length, String image_type, PlistRefOut result) {
    return LibIMobileDeviceJNI.mobile_image_mounter_mount_image(MobileImageMounterClientRef.getCPtr(client), image_path, image_signature, signature_length, image_type, PlistRefOut.getCPtr(result), result);
  }

  public static short mobile_image_mounter_hangup(MobileImageMounterClientRef client) {
    return LibIMobileDeviceJNI.mobile_image_mounter_hangup(MobileImageMounterClientRef.getCPtr(client));
  }


  public static PlistRef toPlistRef(long ptr) {
    return new PlistRef(ptr, false);
  }

}