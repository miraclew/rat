package com.apps2k.rat;

import java.util.Calendar;
import java.util.TimeZone;

import com.apps2k.rat.RatProtocol.Continent;
import com.apps2k.rat.RatProtocol.DayOfWeek;
import com.apps2k.rat.RatProtocol.LocalTime;
import com.apps2k.rat.RatProtocol.LocalTimes;
import com.apps2k.rat.RatProtocol.Location;
import com.apps2k.rat.RatProtocol.Locations;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import static java.util.Calendar.*;


public class RatServerHandler extends SimpleChannelInboundHandler<Locations> {

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Locations locations)
			throws Exception {		
		long currentTime = System.currentTimeMillis();
		LocalTimes.Builder builder = LocalTimes.newBuilder();
		for (Location l : locations.getLocationList()) {
			TimeZone tz = TimeZone.getTimeZone(
					toString(l.getContinent()) + '/' + l.getCity());
					Calendar calendar = getInstance(tz);
					calendar.setTimeInMillis(currentTime);
					builder.addLocalTime(LocalTime.newBuilder().
					setYear(calendar.get(YEAR)).
					setMonth(calendar.get(MONTH) + 1).
					setDayOfMonth(calendar.get(DAY_OF_MONTH)).
					setDayOfWeek(DayOfWeek.valueOf(calendar.get(DAY_OF_WEEK))).
					setHour(calendar.get(HOUR_OF_DAY)).
					setMinute(calendar.get(MINUTE)).
					setSecond(calendar.get(SECOND)).build());

		}
		ctx.write(builder.build());
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private static String toString(Continent c) {
		return c.name().charAt(0) + c.name().toLowerCase().substring(1);
	}
}
