using System;
using System.Threading;
using Xunit;

namespace UnitTests
{
    public class UnitTest1
    {
        [Fact]
        public async void SlowTest()
        {
            Thread.Sleep(5000);
            Assert.True(true);
        }
    }
}