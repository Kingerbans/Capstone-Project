package com.example.webrtcdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.webrtcdemo.Handler.SocketHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;

import io.socket.emitter.Emitter;


public class CallActivity extends AppCompatActivity{
    private static final String localThreadName = "CaptureThread";
    private static final String localVideoTrackId = "100";
    private static final String audioVideoTrackId = "101";
    private static final String mediaStreamLable = "mediaStream";
    private static final String SDP_MID = "sdpMid";
    private static final String SDP_M_LINE_INDEX = "sdpMLineIndex";
    private static final String SDP = "sdp";
    private static final String CALLACCEPT = "call-accept";
    private static final String CREATEOFFER = "createoffer";
    private static final String CREATERECEIVE = "call-receive";
    private static final String OFFER = "offer";
    private static final String ANSWER = "answer";
    private static final String CANDIDATE = "candidate";
    private static final String CALL = "call";

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    PeerConnectionFactory peerConnectionFactory;
    PeerConnectionFactory.Options options;
    EglBase.Context eglBaseContext;
    DefaultVideoEncoderFactory defaultVideoEncoderFactory;
    DefaultVideoDecoderFactory defaultVideoDecoderFactory;
    SurfaceTextureHelper surfaceTextureHelper;
    VideoCapturer videoCapturer;
    VideoSource videoSource;
    VideoTrack videoTrack;
    AudioSource audioSource;
    AudioTrack audioTrack;
    VideoTrack remoteVideoTrack;
    PeerConnection peerConnection;
    SurfaceViewRenderer localView;
    SurfaceViewRenderer remoteView;
    MediaStream localMediaStream;
    LinearLayout linearLayout;
    ImageView btnAccept, btnCamera, btnMic;
    TextView textView;
    boolean check;
    private boolean createOffer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        firebaseAuth = FirebaseAuth.getInstance();

        check = getIntent().getExtras().getBoolean("Check-Caller");

        eglBaseContext = EglBase.create().getEglBaseContext();

        // create PeerConnectionFactory
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions
                .builder(this)
                .createInitializationOptions());
        options = new PeerConnectionFactory.Options();
        defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(eglBaseContext, true, true);
        defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(eglBaseContext);
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();

        surfaceTextureHelper = SurfaceTextureHelper.create(localThreadName, eglBaseContext);
        // create VideoCapturer
        videoCapturer = createCameraCapturer(true);
        videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());
        videoCapturer.startCapture(480, 640, 30);

        Permissions();

        localView = findViewById(R.id.localView);
        localView.setMirror(true);
        localView.init(eglBaseContext, null);

        // create VideoTrack
        videoTrack = peerConnectionFactory.createVideoTrack(localVideoTrackId, videoSource);
        // display in localView
        videoTrack.addSink(localView);


        remoteView = findViewById(R.id.remoteView);
        remoteView.setMirror(false);
        remoteView.init(eglBaseContext, null);

        // create AudioTrack
        audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        audioTrack = peerConnectionFactory.createAudioTrack(audioVideoTrackId, audioSource);

        // add to Stream
        localMediaStream = peerConnectionFactory.createLocalMediaStream(mediaStreamLable);
        localMediaStream.addTrack(videoTrack);
        localMediaStream.addTrack(audioTrack);

        videoTrack.setEnabled(false);
        audioTrack.setEnabled(false);

        btnCamera = findViewById(R.id.btnCamera);
        btnMic = findViewById(R.id.btnMic);

        btnCamera.setTag(R.drawable.camera_off_icon);
        btnMic.setTag(R.drawable.mic_off_icon);

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int drawableId = (Integer)btnMic.getTag();
                System.out.println("<-------------------------------->");
                System.out.println(drawableId);
                if (drawableId == 2131230877) {
                    audioTrack.setEnabled(false);
                    btnMic.setImageResource(R.drawable.mic_on_icon);
                    btnMic.setTag(R.drawable.mic_on_icon);
                } else {
                    audioTrack.setEnabled(true);
                    btnMic.setImageResource(R.drawable.mic_off_icon);
                    btnMic.setTag(R.drawable.mic_off_icon);
                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int drawableId = (Integer)btnCamera.getTag();
                System.out.println("<-------------------------------->");
                System.out.println(drawableId);
                if (drawableId == 2131230821) {
                    videoTrack.setEnabled(false);
                    btnCamera.setImageResource(R.drawable.camera_on_icon);
                    btnCamera.setTag(R.drawable.camera_on_icon);
                } else {
                    videoTrack.setEnabled(true);
                    btnCamera.setImageResource(R.drawable.camera_off_icon);
                    btnCamera.setTag(R.drawable.camera_off_icon);
                }
            }
        });

        call();

        if (!check) {
            SocketHandler.getSocket().emit(CREATERECEIVE);
            linearLayout = findViewById(R.id.callLayout);
            btnAccept = findViewById(R.id.btnAccept);
            textView = findViewById(R.id.incomingCallTxt);

            linearLayout.setVisibility(View.VISIBLE);
            textView.setText(getIntent().getExtras().getString("fullname") + " is calling .....");

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoTrack.setEnabled(true);
                    audioTrack.setEnabled(true);
                    SocketHandler.getSocket().emit(CALLACCEPT);
                    linearLayout.setVisibility(View.GONE);
                }
            });
        }
        else {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("fullname");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    SocketHandler.getSocket().emit(CALL, snapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void Permissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 50); }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 60); }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.MODIFY_AUDIO_SETTINGS}, 70); }
    }

    private VideoCapturer createCameraCapturer(boolean isFront) {
        Camera1Enumerator enumerator = new Camera1Enumerator(false);
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        for (String deviceName : deviceNames) {
            if (isFront ? enumerator.isFrontFacing(deviceName) : enumerator.isBackFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private void call() {
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        peerConnection = peerConnectionFactory.createPeerConnection(
                iceServers,
                new MediaConstraints(),
                peerConnectionObserver);

        peerConnection.addStream(localMediaStream);
        SocketHandler.getSocket().on(CREATEOFFER, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                createOffer = true;
                peerConnection.createOffer(sdpObserver, new MediaConstraints());
            }
        }).on(OFFER, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                try {
                    JSONObject obj = (JSONObject) args[0];
                    SessionDescription sdp = new SessionDescription(SessionDescription.Type.OFFER, obj.getString(SDP));
                    peerConnection.setRemoteDescription(sdpObserver, sdp);
                    peerConnection.createAnswer(sdpObserver, new MediaConstraints());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on(ANSWER, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                try {
                    JSONObject obj = (JSONObject) args[0];
                    SessionDescription sdp = new SessionDescription(SessionDescription.Type.ANSWER, obj.getString(SDP));
                    peerConnection.setRemoteDescription(sdpObserver, sdp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on(CANDIDATE, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                try {
                    JSONObject obj = (JSONObject) args[0];
                    peerConnection.addIceCandidate(new IceCandidate(obj.getString(SDP_MID), obj.getInt(SDP_M_LINE_INDEX), obj.getString(SDP)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on(CALLACCEPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                videoTrack.setEnabled(true);
                audioTrack.setEnabled(true);
            }
        });
    }

    SdpObserver sdpObserver = new SdpObserver() {

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            peerConnection.setLocalDescription(sdpObserver, sessionDescription);
            try {
                JSONObject obj = new JSONObject();
                obj.put(SDP, sessionDescription.description);
                if (createOffer) {
                    SocketHandler.getSocket().emit(OFFER, obj);
                } else {
                    SocketHandler.getSocket().emit(ANSWER, obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSetSuccess() {

        }

        @Override
        public void onCreateFailure(String s) {

        }

        @Override
        public void onSetFailure(String s) {

        }
    };

    PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d("RTCAPP", "onSignalingChange:" + signalingState.toString());
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d("RTCAPP", "onIceConnectionChange:" + iceConnectionState.toString());
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            try {
                JSONObject obj = new JSONObject();
                obj.put(SDP_MID, iceCandidate.sdpMid);
                obj.put(SDP_M_LINE_INDEX, iceCandidate.sdpMLineIndex);
                obj.put(SDP, iceCandidate.sdp);
                SocketHandler.getSocket().emit(CANDIDATE, obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            remoteVideoTrack = mediaStream.videoTracks.get(0);
            runOnUiThread(() -> {
                remoteVideoTrack.addSink(remoteView);
            });
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

        }
    };
}