package com.epam.cdp.aws.fileuploader;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;

public class FileUploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploader.class);
    private static final String S3_BUCKET_NAME = "huge-objects-godagen";
    private static final long MULTIPART_UPLOAD_THRESHOLD = (long) (5 * 1024 * 1025);


    public static void main(String[] args) {
        ArgumentParser argumentParser = ArgumentParsers.newFor("File").build()
                .defaultHelp(true)
                .description("Upload given file to S3 bucket");

        argumentParser.addArgument("-f", "--file")
                .dest("file")
                .required(true)
                .help("File name and path to upload");

        Namespace namespace = null;
        try {
            namespace = argumentParser.parseArgs(args);
        } catch (ArgumentParserException exc) {
            argumentParser.handleError(exc);
            System.exit(1);
        }

        String fileName = namespace.getString("file");
        File file = Paths.get(fileName).toFile();

        if (!file.exists()) {
            throw new IllegalArgumentException("File: " + file.getName() + "doesn't exist");
        }

        new FileUploader().uploadFile(file);
    }

    private AmazonS3 amazonS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.EU_CENTRAL_1)
                .build();
    }

    private TransferManager transferManager() {
        return TransferManagerBuilder
                .standard()
                .withS3Client(amazonS3Client())
                .withMultipartUploadThreshold(MULTIPART_UPLOAD_THRESHOLD)
                .build();
    }

    private void uploadFile(File file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.addUserMetadata("x-amz-meta-huge-object", "true");
        PutObjectRequest putObjectRequest = new PutObjectRequest(S3_BUCKET_NAME, file.getName(), file)
                .withMetadata(objectMetadata);

        TransferManager transferManager = transferManager();

        Upload upload = transferManager.upload(putObjectRequest);
        upload.addProgressListener(createProgressListener(upload, file.getName()));
        try {
            upload.waitForCompletion();
        } catch (InterruptedException exc) {
            LOGGER.error("Error occurred while uploading file: ", exc);
        } finally {
            transferManager.shutdownNow(false);
        }
    }

    private ProgressListener createProgressListener(Transfer transfer, String fileName) {
        return new ProgressListener() {
            private ProgressEventType previousType;
            private double previousTransferred;

            @Override
            public synchronized void progressChanged(ProgressEvent progressEvent) {
                ProgressEventType eventType = progressEvent.getEventType();
                if (previousType != eventType) {
                    LOGGER.info(String.format("Upload progress event {%s}: %s", fileName, eventType));
                    previousType = eventType;
                }

                double transferred = transfer.getProgress().getPercentTransferred();
                if (transferred >= (previousTransferred + 10.0)) {
                    LOGGER.info(String.format("Upload percentage (%s): %.0f%%", fileName, transferred));
                    previousTransferred = transferred;
                }
            }
        };
    }


}
