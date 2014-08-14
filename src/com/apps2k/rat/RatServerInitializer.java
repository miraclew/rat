package com.apps2k.rat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;

public class RatServerInitializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	
	public RatServerInitializer(SslContext sslCtx) {
		this.sslCtx = sslCtx;
	}
	
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		if (sslCtx != null) {
			p.addLast(sslCtx.newHandler(ch.alloc()));
		}
		
		p.addLast(new ProtobufVarint32FrameDecoder());
		p.addLast(new ProtobufDecoder(RatProtocol.Location.getDefaultInstance()));
		
		p.addLast(new ProtobufVarint32LengthFieldPrepender());
		p.addLast(new ProtobufEncoder());
		
		p.addLast(new RatServerHandler());
	}



}
