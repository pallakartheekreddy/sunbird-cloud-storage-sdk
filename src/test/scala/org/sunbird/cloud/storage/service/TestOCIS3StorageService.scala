package org.sunbird.cloud.storage.service

import org.scalatest.{FlatSpec, Matchers}
import org.sunbird.cloud.storage.conf.AppConf
import org.sunbird.cloud.storage.factory.{StorageConfig, StorageServiceFactory}

class TestOCIS3StorageService  extends FlatSpec with Matchers {

  it should "test for OCIS3 storage" in {

    val ociS3Service = StorageServiceFactory.getStorageService(StorageConfig("oci", AppConf.getStorageKey("oci"), AppConf.getStorageSecret("oci"), Option("https://ax2cel5zyviy.compat.objectstorage.ap-hyderabad-1.oraclecloud.com")))

    val storageContainer = AppConf.getConfig("oci_storage_container")
    println("Key : " + AppConf.getStorageKey("oci"))
    println("Secret : " + AppConf.getStorageSecret("oci"))
    println("Storage: " + storageContainer)

    ociS3Service.upload(storageContainer, "src/test/resources/test-data.log", "testUpload/test-blob.log")
    ociS3Service.download(storageContainer, "testUpload/test-blob.log", "src/test/resources/test-s3/")

    // upload directory
    println("url of folder", ociS3Service.upload(storageContainer, "src/test/resources/1234/", "testUpload/1234/", Option(true)))

    // downlaod directory
    ociS3Service.download(storageContainer, "testUpload/1234/", "src/test/resources/test-s3/", Option(true))

    println("OCIS3 signed url", ociS3Service.getSignedURL(storageContainer, "testUpload/test-blob.log", Option(600)))

    val blob = ociS3Service.getObject(storageContainer, "testUpload/test-blob.log")
    println("blob details: ", blob)

    println("upload public url", ociS3Service.upload(storageContainer, "src/test/resources/test-data.log", "testUpload/test-data-public.log", Option(true)))
    println("upload public with expiry url", ociS3Service.upload(storageContainer, "src/test/resources/test-data.log", "testUpload/test-data-with-expiry.log", Option(false)))
    println("signed path to upload from external client", ociS3Service.getSignedURL(storageContainer, "testUpload/test-data-public1.log", Option(600), Option("w")))

    val keys = ociS3Service.searchObjectkeys(storageContainer, "testUpload/1234/")
    keys.foreach(f => println(f))
    val blobs = ociS3Service.searchObjects(storageContainer, "testUpload/1234/")
    blobs.foreach(f => println(f))

    val objData = ociS3Service.getObjectData(storageContainer, "testUpload/test-blob.log")
    objData.length should be(18)

    // delete directory
    ociS3Service.deleteObject(storageContainer, "testUpload/1234/", Option(true))
    ociS3Service.deleteObject(storageContainer, "testUpload/test-blob.log")

    ociS3Service.upload(storageContainer, "src/test/resources/test-extract.zip", "testUpload/test-extract.zip")
    ociS3Service.copyObjects(storageContainer, "testUpload/test-extract.zip", storageContainer, "testDuplicate/test-extract.zip")

    ociS3Service.extractArchive(storageContainer, "testUpload/test-extract.zip", "testUpload/test-extract/")


  }

}